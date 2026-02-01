package com.wdreslerski.ecommerce.user;

import com.wdreslerski.ecommerce.common.ApiResponse;
import jakarta.validation.Valid;
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
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User user = userMapper.toEntity(registerRequest);
        AuthResponse authResponse = userService.register(user);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = userService.login(authRequest);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", authResponse));
    }
}
