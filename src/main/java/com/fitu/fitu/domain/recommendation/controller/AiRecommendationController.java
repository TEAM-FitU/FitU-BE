package com.fitu.fitu.domain.recommendation.controller;

import com.fitu.fitu.domain.recommendation.dto.request.RecommendOutfitRequest;
import com.fitu.fitu.domain.recommendation.dto.response.AiRecommendationResponse;
import com.fitu.fitu.domain.recommendation.entity.AiRecommendation;
import com.fitu.fitu.domain.recommendation.service.AiRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/recommendation")
@RestController
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    @PostMapping
    public AiRecommendationResponse recommendOutfit(@Valid @RequestBody final RecommendOutfitRequest requestDto) {
        final AiRecommendation aiRecommendation = aiRecommendationService.recommendOutfit(requestDto);
        return new AiRecommendationResponse(aiRecommendation);
    }
}
