package com.fitu.fitu.infra.ai.recommendation;

import lombok.Getter;

import java.util.List;

@Getter
public class AiRecommendationResponse {
    private Header header;
    private Body body;

    @Getter
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Getter
    public static class Body {
        private String summary;
        private List<RecommendationItem> result;
    }

    @Getter
    public static class RecommendationItem {
        private String combination;
        private String selected;
        private String reason;
        private String virtualTryonImage;
        private String virtualTryonError;
    }
}
