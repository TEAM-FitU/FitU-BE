package com.fitu.fitu.domain.recommendation.dto.response;

import java.util.List;

public record AiRecommendationResponse(
        String summary,
        List<recommendationContent> contents
) {

    public record recommendationContent(
            String text,
            String imageUrl
    ) { }
}
