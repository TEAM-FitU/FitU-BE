package com.fitu.fitu.global.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /* COMMON ERROR */
    INTERNAL_SERVER_ERROR(500, "COMMON001", "Internal Server Error"),
    INVALID_INPUT_VALUE(400, "COMMON002", "Invalid Input Value"),
    ENTITY_NOT_FOUND(400, "COMMON003", "Entity Not Found"),

    /* USER ERROR */
    USER_NOT_FOUND(400, "USER001", "User Not Found"),

    /* CLOTHES ERROR */
    CLOTHES_NOT_FOUND(400, "CLOTHES001", "Clothes Not Found"),

    /* RECOMMENDATION ERROR */
    NO_RECOMMENDATION(500, "RECOMMENDATION001", "No Recommendation"),
    AI_RECOMMENDATION_SERVER_ERROR(500, "RECOMMENDATION002", "AI Recommendation Server Error");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
