package com.fitu.fitu.domain.recommendation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String summary;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "ai_recommendation_text_1", columnDefinition = "LONGTEXT")),
            @AttributeOverride(name = "imageUrl", column = @Column(name = "ai_recommendation_image_url_1"))
    })
    private Content content1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "ai_recommendation_text_2", columnDefinition = "LONGTEXT")),
            @AttributeOverride(name = "imageUrl", column = @Column(name = "ai_recommendation_image_url_2"))
    })
    private Content content2;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "text", column = @Column(name = "ai_recommendation_text_3", columnDefinition = "LONGTEXT")),
            @AttributeOverride(name = "imageUrl", column = @Column(name = "ai_recommendation_image_url_3"))
    })
    private Content content3;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public AiRecommendation(final String userId, final String summary,
                            final Content content1, final Content content2, final Content content3) {
        this.userId = userId;
        this.summary = summary;
        this.content1 = content1;
        this.content2 = content2;
        this.content3 = content3;
    }
}
