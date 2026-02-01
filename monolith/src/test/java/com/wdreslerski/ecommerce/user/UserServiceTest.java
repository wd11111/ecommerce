package com.wdreslerski.ecommerce.user;

import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserService userService;

    private User userRequest;

    @BeforeEach
    void setUp() {
        userRequest = User.builder()
                .email("test@example.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    @Test
    void register_ShouldRegisterUser_WhenEmailIsUnique() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        // Act
        AuthResponse response = userService.register(userRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.register(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        // Arrange
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("jwt-token");

        // Act
        AuthResponse response = userService.login(authRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getAccessToken());
    }
}
