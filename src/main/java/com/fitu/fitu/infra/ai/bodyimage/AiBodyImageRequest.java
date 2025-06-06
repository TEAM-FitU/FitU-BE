package com.fitu.fitu.infra.ai.bodyimage;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AiBodyImageRequest(
        @JsonProperty("s3url")
        String s3Url
) {
}