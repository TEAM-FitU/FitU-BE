package com.fitu.fitu.domain.user.service;

import com.fitu.fitu.domain.user.dto.request.ProfileRequest;
import com.fitu.fitu.domain.user.entity.User;
import com.fitu.fitu.domain.user.exception.UserNotFoundException;
import com.fitu.fitu.domain.user.repository.UserRepository;
import com.fitu.fitu.global.util.FileValidator;
import com.fitu.fitu.infra.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final FileValidator fileValidator;

    @Transactional
    public User registerProfile(final ProfileRequest requestDto) {
        String userId = generateUserId();

        final User user = requestDto.toEntity(userId);

        if (user.getBodyImageUrl() != null) {
            user.setBodyImageUrl(s3Service.copy(user.getBodyImageUrl(), "bodyImage/"));
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(final String userId, final ProfileRequest requestDto) {
        final User user = findById(userId);

        user.setAge(requestDto.age());
        user.setGender(requestDto.gender());
        user.setHeight(requestDto.height());
        user.setWeight(requestDto.weight());
        user.setSkinTone(requestDto.skinTone());

        if (!Objects.equals(user.getBodyImageUrl(), requestDto.bodyImageUrl())) {
            updateBodyImage(user, requestDto.bodyImageUrl());
        }

        return user;
    }

    @Transactional
    public String analyzeBodyImage(final MultipartFile file) {
        fileValidator.validateImage(file);

//        TODO 전신 사진 유효성 검사 AI API 호출

        return s3Service.upload("temp/", file);
    }

    @Transactional(readOnly = true)
    public User getProfile(final String userId) {
        final User user = findById(userId);

        return user;
    }

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }

    private User findById(final String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void updateBodyImage(final User user, final String bodyImageUrl) {
        if (bodyImageUrl != null) {
            user.setBodyImageUrl(s3Service.copy(bodyImageUrl, "bodyImage/"));
        } else {
            user.setBodyImageUrl(null);
        }
    }
}