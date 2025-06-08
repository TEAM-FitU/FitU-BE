package com.fitu.fitu.domain.recommendation.service;

import com.fitu.fitu.domain.clothes.exception.ClothesNotFoundException;
import com.fitu.fitu.domain.recommendation.dto.request.RecommendOutfitRequest;
import com.fitu.fitu.domain.recommendation.entity.AiRecommendation;
import com.fitu.fitu.domain.recommendation.entity.Content;
import com.fitu.fitu.domain.recommendation.exception.NoRecommendationException;
import com.fitu.fitu.domain.recommendation.repository.AiRecommendationRepository;
import com.fitu.fitu.domain.recommendation.service.WeatherService.Weather;
import com.fitu.fitu.domain.user.exception.UserNotFoundException;
import com.fitu.fitu.global.error.ErrorCode;
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
    private final AiRecommendationApiClient aiRecommendationApiClient;
    private final AiRecommendationRepository aiRecommendationRepository;

    private static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    private static final String ERROR_NO_CLOTHES = "NO_CLOTHES";
    private static final String ERROR_NO_MATCH = "NO_MATCH";

    @Transactional
    public AiRecommendation recommendOutfit(final String userId, final RecommendOutfitRequest requestDto) {
        final Weather weather = weatherService.getWeather(requestDto.date(), requestDto.place());

        final AiRecommendationResponse aiRecommendationResponse = aiRecommendationApiClient.getAiRecommendation(userId, requestDto, weather);

        validateAiRecommendationResponse(aiRecommendationResponse);

        final AiRecommendation aiRecommendation = getAiRecommendation(userId, aiRecommendationResponse);

        return aiRecommendationRepository.save(aiRecommendation);
    }

    private void validateAiRecommendationResponse(final AiRecommendationResponse response) {
        final String resultMsg = response.getHeader().getResultMsg();

        if (resultMsg.equals(ERROR_USER_NOT_FOUND)) {
            throw new UserNotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        if (resultMsg.equals(ERROR_NO_CLOTHES)) {
            throw new ClothesNotFoundException(ErrorCode.CLOTHES_NOT_FOUND);
        }

        if (resultMsg.equals(ERROR_NO_MATCH)) {
            throw new NoRecommendationException(ErrorCode.NO_RECOMMENDATION);
        }
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
