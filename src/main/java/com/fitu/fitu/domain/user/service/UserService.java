package com.fitu.fitu.domain.user.service;

import com.fitu.fitu.domain.user.dto.request.ProfileRequest;
import com.fitu.fitu.domain.user.entity.User;
import com.fitu.fitu.domain.user.exception.UserNotFoundException;
import com.fitu.fitu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User registerProfile(final ProfileRequest requestDto) {
        String userId = generateUserId();

        final User user = requestDto.toEntity(userId);

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
        user.setBodyImageUrl(requestDto.bodyImageUrl());

        return user;
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
}