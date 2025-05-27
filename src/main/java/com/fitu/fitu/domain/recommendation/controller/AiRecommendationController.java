package com.fitu.fitu.domain.recommendation.controller;

import com.fitu.fitu.domain.recommendation.service.AiRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/recommendation")
@RestController
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    @PostMapping
    public void recommendStyling() {

    }
}
