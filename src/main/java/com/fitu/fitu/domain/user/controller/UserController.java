package com.fitu.fitu.domain.user.controller;

import com.fitu.fitu.domain.user.dto.request.ProfileRequest;
import com.fitu.fitu.domain.user.dto.response.ProfileResponse;
import com.fitu.fitu.domain.user.entity.User;
import com.fitu.fitu.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/profile")
    public ProfileResponse registerProfile(@Valid @RequestBody final ProfileRequest requestDto) {
        final User user = userService.registerProfile(requestDto);

        return ProfileResponse.of(user);
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@RequestHeader("Fitu-User-UUID") final String userId) {
        final User user = userService.getProfile(userId);

        return ProfileResponse.of(user);
    }

    @PatchMapping("/profile")
    public ProfileResponse updateProfile(@RequestHeader("Fitu-User-UUID") final String userId,
                                       @Valid @RequestBody final ProfileRequest requestDto) {
        final User user = userService.updateProfile(userId, requestDto);

        return ProfileResponse.of(user);
    }
}