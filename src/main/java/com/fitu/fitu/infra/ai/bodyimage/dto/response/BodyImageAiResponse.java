package com.fitu.fitu.infra.ai.bodyimage.dto.response;

import java.util.List;

public record BodyImageAiResponse(
        List<String> warnings
) {
}