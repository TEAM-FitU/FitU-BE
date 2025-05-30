package com.fitu.fitu.infra.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class WeatherApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String baseUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final List<Integer> BASE_HOURS = List.of(2, 5, 8, 11, 14, 17, 20, 23);
    private static final int MINUTE_OFFSET = 10;

    @Value("${infra.weather.api.service-key}")
    private String serviceKey;

    public WeatherDataDto getWeather(final int nx, final int ny) {
        final String requestUrl = getRequestUrl(LocalDateTime.now(), nx, ny);

        return restTemplate.getForEntity(requestUrl, WeatherDataDto.class)
                .getBody();
    }

    private String getRequestUrl(final LocalDateTime now, final int nx, final int ny) {
        final BaseDateTime baseDateTime = getBaseDateTime(now);

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "1000")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime.baseDate)
                .queryParam("base_time", baseDateTime.baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build()
                .toUriString();
    }

    private BaseDateTime getBaseDateTime(LocalDateTime now) {
        now = now.minusMinutes(MINUTE_OFFSET);

        int nowHour = now.getHour();
        int baseHour;

        if (nowHour < BASE_HOURS.getFirst()) {
            now = now.minusDays(1);
            baseHour = BASE_HOURS.get(BASE_HOURS.getLast());
        } else {
            baseHour = BASE_HOURS.stream()
                    .filter(h -> nowHour >= h)
                    .max(Integer::compareTo)
                    .orElse(BASE_HOURS.getFirst());
        }

        final String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        final String baseTime = String.format("%02d00", baseHour);

        return new BaseDateTime(baseDate, baseTime);
    }

    private record BaseDateTime(String baseDate, String baseTime) {}
}
