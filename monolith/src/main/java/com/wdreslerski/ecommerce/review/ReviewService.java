package com.wdreslerski.ecommerce.review;

import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
import com.wdreslerski.ecommerce.product.Product;
import com.wdreslerski.ecommerce.product.ProductRepository;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewDTO addReview(Long userId, Long productId, ReviewDTO reviewDTO) {
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new BadRequestException("User has already reviewed this product");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Review review = reviewMapper.toEntity(reviewDTO);
        review.setUser(user);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }
}
