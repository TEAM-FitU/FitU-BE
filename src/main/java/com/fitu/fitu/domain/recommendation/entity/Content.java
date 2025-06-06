package com.fitu.fitu.domain.recommendation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Content {

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String combinationClothes;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String selectedClothes;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String text;

    @Column
    private String imageUrl;

    @Builder
    public Content(final String combinationClothes, final String selectedClothes, final String text, final String imageUrl) {
        this.combinationClothes = combinationClothes;
        this.selectedClothes = selectedClothes;
        this.text = text;
        this.imageUrl = imageUrl;
    }
}
