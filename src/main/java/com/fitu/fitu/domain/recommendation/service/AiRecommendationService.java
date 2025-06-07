package com.fitu.fitu.domain.recommendation.service;

import com.fitu.fitu.domain.recommendation.dto.request.RecommendOutfitRequest;
import com.fitu.fitu.domain.recommendation.entity.AiRecommendation;
import com.fitu.fitu.domain.recommendation.entity.Content;
import com.fitu.fitu.domain.recommendation.repository.AiRecommendationRepository;
import com.fitu.fitu.domain.recommendation.service.WeatherService.Weather;
import com.fitu.fitu.infra.ai.recommendation.AiRecommendationApiClient;
import com.fitu.fitu.infra.ai.recommendation.AiRecommendationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AiRecommendationService {

    private final WeatherService weatherService;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final AiRecommendationApiClient aiRecommendationApiClient;

    @Transactional
    public AiRecommendation recommendOutfit(final String userId, final RecommendOutfitRequest requestDto) {
        final Weather weather = weatherService.getWeather(requestDto.time(), requestDto.place());

        final AiRecommendationResponse aiRecommendationResponse = aiRecommendationApiClient.getAiRecommendation(userId, requestDto, weather);

        final AiRecommendation aiRecommendation = getAiRecommendation(userId, aiRecommendationResponse);

        return aiRecommendationRepository.save(aiRecommendation);
    }

    private AiRecommendation getAiRecommendation(final String userId, final AiRecommendationResponse aiRecommendationResponse) {
        final AiRecommendationResponse.Body body = aiRecommendationResponse.getBody();
        final List<AiRecommendationResponse.RecommendationItem> result = body.getResult();

        return AiRecommendation.builder()
                .userId(userId)
                .summary(body.getSummary())
                .content1(new Content(result.getFirst().getCombination(), result.getFirst().getSelected(), result.getFirst().getReason(), result.getFirst().getVirtualTryonImage()))
                .content2(new Content(result.get(1).getCombination(), result.get(1).getSelected(), result.get(1).getReason(), result.get(1).getVirtualTryonImage()))
                .content3(new Content(result.getLast().getCombination(), result.getLast().getSelected(), result.getLast().getReason(), result.getLast().getVirtualTryonImage()))
                .build();
    }
}
