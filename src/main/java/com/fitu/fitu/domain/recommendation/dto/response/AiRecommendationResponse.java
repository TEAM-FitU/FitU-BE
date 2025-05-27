package com.fitu.fitu.domain.recommendation.dto.response;

import com.fitu.fitu.domain.recommendation.entity.AiRecommendation;
import com.fitu.fitu.domain.recommendation.entity.Content;

import java.util.List;

public record AiRecommendationResponse(
        String summary,
        List<RecommendationContent> contents
) {

    public record RecommendationContent(
            String text,
            String imageUrl
    ) {

        public RecommendationContent(final Content content) {
            this(content.getText(), content.getImageUrl());
        }
    }

    public AiRecommendationResponse(final AiRecommendation aiRecommendation) {
        this(aiRecommendation.getSummary(),
             List.of(
                     new RecommendationContent(aiRecommendation.getContent1()),
                     new RecommendationContent(aiRecommendation.getContent2()),
                     new RecommendationContent(aiRecommendation.getContent3())
             )
        );
    }
}
