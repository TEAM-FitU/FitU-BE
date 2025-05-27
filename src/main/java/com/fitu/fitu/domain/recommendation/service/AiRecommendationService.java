package com.fitu.fitu.domain.recommendation.service;

import com.fitu.fitu.domain.recommendation.repository.AiRecommendationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AiRecommendationService {

    private final AiRecommendationRepository aiRecommendationRepository;

    @Transactional
    public void recommendStyling() {

    }
}
