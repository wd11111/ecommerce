package com.wdreslerski.ecommerce.security;

import com.wdreslerski.ecommerce.user.Role;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenFound() {
        // Arrange
        User user = User.builder()
                .email("test@example.com")
                .password("password")
                .role(Role.CUSTOMER)
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown@example.com"));
    }
}
