package com.fitu.fitu.infra.weather;

import com.fitu.fitu.infra.weather.util.BaseDateTimeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Component
public class ShortTermWeatherApiClient {

    private BaseDateTimeGenerator baseDateTimeGenerator;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    @Value("${infra.weather.api.service-key}")
    private String serviceKey;

    public ShortTermWeatherResponse getWeather(final int nx, final int ny) {
        final String requestUrl = getRequestUrl(LocalDateTime.now(), nx, ny);

        return restTemplate.getForEntity(requestUrl, ShortTermWeatherResponse.class)
                .getBody();
    }

    private String getRequestUrl(final LocalDateTime now, final int nx, final int ny) {
        final BaseDateTimeGenerator.BaseDateTime baseDateTime = baseDateTimeGenerator.generateBaseDateTimeForShortTerm(now);

        return UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", "1")
                .queryParam("numOfRows", "1000")
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDateTime.baseDate())
                .queryParam("base_time", baseDateTime.baseTime())
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .build()
                .toUriString();
    }
}
