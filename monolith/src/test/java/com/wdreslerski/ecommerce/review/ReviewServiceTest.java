package com.wdreslerski.ecommerce.review;

import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.product.Product;
import com.wdreslerski.ecommerce.product.ProductRepository;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private Product product;
    private ReviewDTO reviewDTO;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        product = Product.builder().id(1L).name("Test Product").build();
        reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great!");

        review = new Review();
        review.setId(1L);
        review.setUser(user);
        review.setProduct(product);
        review.setRating(5);
    }

    @Test
    void addReview_ShouldSaveReview_WhenNotDuplicate() {
        // Arrange
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewMapper.toEntity(reviewDTO)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // Act
        ReviewDTO result = reviewService.addReview(1L, 1L, reviewDTO);

        // Assert
        assertNotNull(result);
        verify(reviewRepository).save(review);
    }

    @Test
    void addReview_ShouldThrowException_WhenDuplicate() {
        // Arrange
        when(reviewRepository.existsByUserIdAndProductId(1L, 1L)).thenReturn(true);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> reviewService.addReview(1L, 1L, reviewDTO));
    }

    @Test
    void getProductReviews_ShouldReturnList() {
        // Arrange
        when(reviewRepository.findByProductId(1L)).thenReturn(Collections.singletonList(review));
        when(reviewMapper.toDTO(review)).thenReturn(reviewDTO);

        // Act
        List<ReviewDTO> results = reviewService.getProductReviews(1L);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }
}
