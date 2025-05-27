package com.fitu.fitu.domain.recommendation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RecommendOutfitRequest(
        @NotBlank
        String situation,
        @NotNull
        LocalDate time,
        @NotBlank
        String place,
        @NotNull
        boolean useOnlyClosetItems
) {
}
