package com.fitu.fitu.domain.user.service;

import com.fitu.fitu.domain.user.dto.request.ProfileRequest;
import com.fitu.fitu.domain.user.entity.User;
import com.fitu.fitu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private String generateUserId() {
        return UUID.randomUUID().toString();
    }
}