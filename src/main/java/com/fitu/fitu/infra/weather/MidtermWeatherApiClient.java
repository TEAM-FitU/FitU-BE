package com.fitu.fitu.infra.weather;

import com.fitu.fitu.infra.weather.util.BaseDateTimeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Component
public class MidtermWeatherApiClient {

    private BaseDateTimeGenerator baseDateTimeGenerator;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String WEATHER_CONDITION_BASE_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidLandFcst";
    private static final String TEMPERATURE_BASE_URL = "http://apis.data.go.kr/1360000/MidFcstInfoService/getMidTa";

    @Value("{infra.weather.api.service-key}")
    private String serviceKey;

    public MidtermWeatherConditionResponse getWeatherCondition(final String regId) {
        final String requestUrl = getRequestUrl(WEATHER_CONDITION_BASE_URL, regId, LocalDateTime.now());

        return restTemplate.getForEntity(requestUrl, MidtermWeatherConditionResponse.class)
                .getBody();
    }

    public MidtermTemperatureResponse getTemperature(final String regId) {
        final String requestUrl = getRequestUrl(TEMPERATURE_BASE_URL, regId, LocalDateTime.now());

        return restTemplate.getForEntity(requestUrl, MidtermTemperatureResponse.class)
                .getBody();
    }

    private String getRequestUrl(final String baseUrl, final String regId, final LocalDateTime now) {
        final String baseDateTime = baseDateTimeGenerator.generateBaseDateTimeForMidTerm(now);

        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "10")
                .queryParam("dataType", "JSON")
                .queryParam("regId", regId)
                .queryParam("tmFc", baseDateTime)
                .build()
                .toUriString();
    }
}
