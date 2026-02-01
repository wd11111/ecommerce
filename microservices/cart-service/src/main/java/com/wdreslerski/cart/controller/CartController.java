package com.wdreslerski.cart.controller;

import com.wdreslerski.cart.common.ApiResponse;
import com.wdreslerski.cart.dto.CartDTO;
import com.wdreslerski.cart.security.UserPrincipal;
import com.wdreslerski.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartDTO>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        CartDTO cart = cartService.getCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart fetched successfully", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDTO>> addToCart(@AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        Long userId = principal.getId();
        CartDTO cart = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Item added to cart", cart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> removeItem(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        Long userId = principal.getId();
        CartDTO cart = cartService.removeItem(userId, productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Item removed from cart", cart));
    }

    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartDTO>> updateQuantity(@AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        Long userId = principal.getId();
        CartDTO cart = cartService.updateQuantity(userId, productId, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart updated", cart));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        cartService.clearCart(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cart cleared"));
    }
}
