package com.fitu.fitu.domain.recommendation.service;

import com.fitu.fitu.infra.weather.*;
import com.fitu.fitu.infra.weather.geocoding.GeocodingApiClient;
import com.fitu.fitu.infra.weather.geocoding.GeocodingResponse;
import com.fitu.fitu.infra.weather.util.MidtermWeatherRegIdMapper;
import com.fitu.fitu.infra.weather.util.MidtermWeatherRegIdMapper.RegId;
import com.fitu.fitu.infra.weather.util.ShortTermWeatherGridConverter;
import com.fitu.fitu.infra.weather.util.ShortTermWeatherGridConverter.Grid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WeatherService {

    private final ShortTermWeatherApiClient shortTermWeatherApiClient;
    private final MidtermWeatherApiClient midtermWeatherApiClient;
    private final GeocodingApiClient geocodingApiClient;

    private static final int SHORT_TERM_THRESHOLD_DAYS = 4;

    public Weather getWeather(final LocalDate targetTime, final String targetPlace) {
        final LocalDate now = LocalDate.now();

        return (isShortTerm(now, targetTime))
                ? getShortTermWeather(targetTime, targetPlace)
                : getMidtermWeather(now, targetTime, targetPlace);
    }

    private boolean isShortTerm(final LocalDate now, final LocalDate targetTime) {
        return ChronoUnit.DAYS.between(now, targetTime) <= SHORT_TERM_THRESHOLD_DAYS;
    }

    private Weather getShortTermWeather(final LocalDate targetTime, final String targetPlace) {
        final Grid grid = getGridForShortTermWeather(targetPlace);

        final ShortTermWeatherResponse weatherResponse = shortTermWeatherApiClient.getWeather(grid.nx(), grid.ny());

        return parseShortTermWeatherResponse(targetTime, weatherResponse);
    }

    private Grid getGridForShortTermWeather(final String targetPlace) {
        final GeocodingResponse geocodingResponse = geocodingApiClient.getCoordinateAndAddress(targetPlace);

        final int lon = (int) Double.parseDouble(geocodingResponse.getDocuments().getFirst().getX());
        final int lat = (int) Double.parseDouble(geocodingResponse.getDocuments().getFirst().getY());

        return ShortTermWeatherGridConverter.convert(lat, lon);
    }

    private Weather parseShortTermWeatherResponse(final LocalDate targetTime, final ShortTermWeatherResponse weatherResponse) {
        final String targetTimeStr = targetTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        final List<ShortTermWeatherResponse.Item> items = weatherResponse.getResponse().getBody().getItems().getItem();
        final List<ShortTermWeatherResponse.Item> filteredItems = items.stream()
                .filter(item -> item.getFcstDate().equals(targetTimeStr))
                .toList();

        final int minTemperature = parseTemperatureFieldForShortTermWeather(filteredItems, "TMN", Integer.MIN_VALUE);
        final int maxTemperature = parseTemperatureFieldForShortTermWeather(filteredItems, "TMX", Integer.MIN_VALUE);

        final Map<String, String> noonItem = filteredItems.stream()
                .filter(item -> "1200".equals(item.getFcstTime()))
                .collect(Collectors.toMap(ShortTermWeatherResponse.Item::getCategory, ShortTermWeatherResponse.Item::getFcstValue, (oldValue, newValue) -> newValue));

        final Temperature temperature = getFinalTemperature(minTemperature, maxTemperature, noonItem, filteredItems);
        final int rainPercent = parseRainPercentFieldForShortTermWeather(noonItem);
        final String weatherCondition = parseWeatherConditionFieldForShortTermWeather(noonItem);

        return new Weather(temperature.minTemperature, temperature.maxTemperature, rainPercent, weatherCondition);
    }

    private int parseTemperatureFieldForShortTermWeather(final List<ShortTermWeatherResponse.Item> filteredItems, final String category, final int orElseValue) {
        return filteredItems.stream()
                .filter(item -> category.equals(item.getCategory()))
                .map(item -> (int) Double.parseDouble(item.getFcstValue()))
                .findFirst()
                .orElse(orElseValue);
    }

    private Temperature getFinalTemperature(final int initMinTemperature, final int initMaxTemperature, final Map<String, String> noonItem, final List<ShortTermWeatherResponse.Item> filteredItems) {
        if (isValuableTemperature(initMinTemperature) && isValuableTemperature(initMaxTemperature)) {
            return new Temperature(initMinTemperature, initMaxTemperature);
        }

        int noonTemperature = Optional.ofNullable(noonItem.get("TMP"))
                .map(Integer::parseInt)
                .orElse(Integer.MIN_VALUE);

        if (isValuableTemperature(noonTemperature)) {
            return new Temperature(noonTemperature, noonTemperature);
        }

        int defaultTemperature = parseTemperatureFieldForShortTermWeather(filteredItems, "TMP", 0);

        return new Temperature(defaultTemperature, defaultTemperature);
    }

    private boolean isValuableTemperature(final int temperature) {
        return temperature != Integer.MIN_VALUE;
    }

    private int parseRainPercentFieldForShortTermWeather(final Map<String, String> noonItem) {
        return Optional.ofNullable(noonItem.get("POP"))
                .map(Integer::parseInt)
                .orElse(0);
    }

    private String parseWeatherConditionFieldForShortTermWeather(final Map<String, String> noonItem) {
        return Optional.ofNullable(noonItem.get("SKY"))
                .map(this::mapSkyCodeToCondition)
                .orElse("알 수 없음");
    }

    private String mapSkyCodeToCondition(final String skyCode) {
        return switch(skyCode) {
            case "1", "2", "3", "4", "5" -> "맑음";
            case "6", "7", "8" -> "구름 많음";
            case "9", "10" -> "흐림";
            default -> "알 수 없음";
        };
    }

    private Weather getMidtermWeather(final LocalDate now, final LocalDate targetTime, final String targetPlace) {
        final RegId regId = getRegIdForMidtermWeather(targetPlace);

        final MidtermTemperatureResponse temperatureResponse = midtermWeatherApiClient.getTemperature(regId.tempRegId());
        final MidtermWeatherConditionResponse weatherConditionResponse = midtermWeatherApiClient.getWeatherCondition(regId.condRegId());

        return parseMidtermWeatherResponse(now, targetTime, temperatureResponse, weatherConditionResponse);
    }

    private RegId getRegIdForMidtermWeather(final String targetPlace) {
        final GeocodingResponse geocodingResponse = geocodingApiClient.getCoordinateAndAddress(targetPlace);

        final String address = geocodingResponse.getDocuments().getFirst().getAddress_name();
        final String city = address.split(" ")[0];

        return MidtermWeatherRegIdMapper.map(city);
    }

    private Weather parseMidtermWeatherResponse(final LocalDate now, final LocalDate targetTime, final MidtermTemperatureResponse temperatureResponse, final MidtermWeatherConditionResponse weatherConditionResponse) {
        final int dayDiff = (int) ChronoUnit.DAYS.between(now, targetTime);

        final MidtermTemperatureResponse.Item temperatureItems = temperatureResponse.getResponse().getBody().getItems().getItem().getFirst();
        final MidtermWeatherConditionResponse.Item weatherConditionItems = weatherConditionResponse.getResponse().getBody().getItems().getItem().getFirst();

        final Temperature temperature = parseTemperatureFieldForMidtermWeather(temperatureItems, dayDiff);
        final int rainPercent = parseRainPercentFieldForMidtermWeather(weatherConditionItems, dayDiff);
        final String weatherCondition = parseWeatherConditionFieldForMidtermWeather(weatherConditionItems, dayDiff);

        return new Weather(temperature.minTemperature, temperature.maxTemperature, rainPercent, weatherCondition);
    }

    private Temperature parseTemperatureFieldForMidtermWeather(final MidtermTemperatureResponse.Item item, int dayDiff) {
        return switch(dayDiff) {
            case 4 -> new Temperature(item.getTaMin4(), item.getTaMax4());
            case 5 -> new Temperature(item.getTaMin5(), item.getTaMax5());
            case 6 -> new Temperature(item.getTaMin6(), item.getTaMax6());
            case 7 -> new Temperature(item.getTaMin7(), item.getTaMax7());
            case 8 -> new Temperature(item.getTaMin8(), item.getTaMax8());
            case 9 -> new Temperature(item.getTaMin9(), item.getTaMax9());
            case 10 -> new Temperature(item.getTaMin10(), item.getTaMax10());
            default -> new Temperature(0, 0);
        };
    }

    private int parseRainPercentFieldForMidtermWeather(final MidtermWeatherConditionResponse.Item item, int dayDiff) {
        return switch(dayDiff) {
            case 4 -> item.getRnSt4Pm();
            case 5 -> item.getRnSt5Pm();
            case 6 -> item.getRnSt6Pm();
            case 7 -> item.getRnSt7Pm();
            case 8 -> item.getRnSt8();
            case 9 -> item.getRnSt9();
            case 10 -> item.getRnSt10();
            default -> 0;
        };
    }

    private String parseWeatherConditionFieldForMidtermWeather(final MidtermWeatherConditionResponse.Item item, int dayDiff) {
        return switch(dayDiff) {
            case 4 -> item.getWf4Pm();
            case 5 -> item.getWf5Pm();
            case 6 -> item.getWf6Pm();
            case 7 -> item.getWf7Pm();
            case 8 -> item.getWf8();
            case 9 -> item.getWf9();
            case 10 -> item.getWf10();
            default -> "알 수 없음";
        };
    }

    public record Weather(int minTemperature, int maxTemperature, int rainPercent, String weatherCondition) {}

    public record Temperature(int minTemperature, int maxTemperature) {}
}
