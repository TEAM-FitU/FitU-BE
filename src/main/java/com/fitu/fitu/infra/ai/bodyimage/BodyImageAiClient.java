package com.fitu.fitu.infra.ai.bodyimage;

import com.fitu.fitu.infra.ai.bodyimage.dto.response.BodyImageAiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Component
public class BodyImageAiClient {
    private final RestClient restClient;

    public BodyImageAiResponse analyzeBodyImage(final String s3Url) {
        return restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/user/profile/image-analysis")
                        .queryParam("s3_url", s3Url)
                        .build())
                .retrieve()
                .body(BodyImageAiResponse.class);
    }
}