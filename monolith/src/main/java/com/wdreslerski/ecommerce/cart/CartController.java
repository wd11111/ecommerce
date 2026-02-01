package com.wdreslerski.ecommerce.cart;

import com.wdreslerski.ecommerce.common.ApiResponse;
import com.wdreslerski.ecommerce.security.JwtTokenProvider;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart fetched successfully", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Long userId = getUserId(userDetails);
        CartDTO cart = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Item added to cart", cart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> remoteItem(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        Long userId = getUserId(userDetails);
        CartDTO cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Item removed from cart", cart));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        Long userId = getUserId(userDetails);
        CartDTO cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart updated", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart cleared"));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
