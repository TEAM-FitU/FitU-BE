package com.fitu.fitu.domain.recommendation.service;

import com.fitu.fitu.domain.recommendation.dto.request.RecommendOutfitRequest;
import com.fitu.fitu.domain.recommendation.entity.AiRecommendation;
import com.fitu.fitu.domain.recommendation.entity.Content;
import com.fitu.fitu.domain.recommendation.repository.AiRecommendationRepository;
import com.fitu.fitu.infra.weather.WeatherApiClient;
import com.fitu.fitu.infra.weather.WeatherDataDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AiRecommendationService {

    private final AiRecommendationRepository aiRecommendationRepository;
    private final WeatherApiClient weatherApiClient;

    @Transactional
    public AiRecommendation recommendOutfit(final RecommendOutfitRequest requestDto) {
        final WeatherDataDto weatherDataDto = weatherApiClient.getWeather(55, 127);

        final AiRecommendation aiRecommendation = getAiRecommendation();

        aiRecommendationRepository.save(aiRecommendation);

        return aiRecommendation;
    }

    // Mock 함수
    private AiRecommendation getAiRecommendation() {
        return AiRecommendation.builder()
                .userId("1")
                .summary("summary")
                .content1(new Content("text", "imageUrl"))
                .content2(new Content("text", "imageUrl"))
                .content3(new Content("text", "imageUrl"))
                .build();
    }
}
