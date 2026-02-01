package com.wdreslerski.identity.controller;

import com.wdreslerski.identity.common.ApiResponse;
import com.wdreslerski.identity.user.AuthRequest;
import com.wdreslerski.identity.user.AuthResponse;
import com.wdreslerski.identity.user.RegisterRequest;
import com.wdreslerski.identity.user.User;
import com.wdreslerski.identity.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @RequestBody @jakarta.validation.Valid RegisterRequest registerRequest) {
        AuthResponse response = userService.register(registerRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest authRequest) {
        AuthResponse response = userService.login(authRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }
}
