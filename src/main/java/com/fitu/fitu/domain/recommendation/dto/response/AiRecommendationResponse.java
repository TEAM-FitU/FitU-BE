package com.fitu.fitu.domain.recommendation.dto.response;

import com.fitu.fitu.domain.recommendation.entity.AiRecommendation;
import com.fitu.fitu.domain.recommendation.entity.Content;

import java.util.List;

public record AiRecommendationResponse(
        String summary,
        List<RecommendationContent> contents
) {

    public record RecommendationContent(
            String clothesCombination,
            String description,
            String imageUrl
    ) {

        public static RecommendationContent of(final Content content) {
            return new RecommendationContent(content.getClothesCombination(), content.getDescription(), content.getImageUrl());
        }
    }

    public static AiRecommendationResponse of(final AiRecommendation aiRecommendation) {
        return new AiRecommendationResponse(aiRecommendation.getSummary(),
             List.of(
                     RecommendationContent.of(aiRecommendation.getContent1()),
                     RecommendationContent.of(aiRecommendation.getContent2()),
                     RecommendationContent.of(aiRecommendation.getContent3())
             )
        );
    }
}
