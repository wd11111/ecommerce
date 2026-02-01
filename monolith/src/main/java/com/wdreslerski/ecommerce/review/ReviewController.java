package com.wdreslerski.ecommerce.review;

import com.wdreslerski.ecommerce.common.ApiResponse;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;

    @PostMapping("/{productId}")
    public ResponseEntity<ApiResponse<ReviewDTO>> addReview(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        Long userId = getUserId(userDetails);
        ReviewDTO createdReview = reviewService.addReview(userId, productId, reviewDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Review added successfully", createdReview));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewDTO>>> getProductReviews(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reviews fetched successfully", reviews));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
