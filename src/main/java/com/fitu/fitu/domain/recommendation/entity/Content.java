package com.fitu.fitu.domain.recommendation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Content {

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String text;

    @Column(nullable = false)
    private String imageUrl;
}
