package com.fitu.fitu.infra.weather.geocoding;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class GeocodingApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL = "https://dapi.kakao.com/v2/local/search/keyword";

    @Value("{infra.geocoding.api.service-key}")
    private String serviceKey;

    public GeocodingResponse getCoordinateAndAddress(final String query) {
        final HttpEntity<Void> headers = getHttpHeaders();
        final String requestUrl = getRequestUrl(query);

        return restTemplate.exchange(requestUrl, HttpMethod.GET, headers, GeocodingResponse.class)
                .getBody();
    }

    private HttpEntity<Void> getHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.set("Authorization", "KakaoAK " + serviceKey);

        return new HttpEntity<>(headers);
    }

    private String getRequestUrl(final String query) {
        return UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("query", query)
                .build()
                .toUriString();
    }
}
