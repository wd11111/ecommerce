package com.wdreslerski.cart.service;

import com.wdreslerski.cart.client.ProductClient;
import com.wdreslerski.cart.common.ApiResponse;
import com.wdreslerski.cart.dto.CartDTO;
import com.wdreslerski.cart.dto.ProductDTO;
import com.wdreslerski.cart.exception.BadRequestException;
import com.wdreslerski.cart.exception.ResourceNotFoundException;
import com.wdreslerski.cart.mapper.CartMapper;
import com.wdreslerski.cart.model.Cart;
import com.wdreslerski.cart.model.CartItem;
import com.wdreslerski.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductClient productClient;
    private final CartMapper cartMapper;

    @Transactional
    public CartDTO getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        // Call Product Service to get product details
        ApiResponse<ProductDTO> productResponse = productClient.getProductById(productId);
        if (productResponse == null || !productResponse.isSuccess() || productResponse.getData() == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        ProductDTO product = productResponse.getData();

        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Not enough stock for product: " + product.getName());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();
            cart.getItems().add(newItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDTO(savedCart);
    }

    @Transactional
    public CartDTO removeItem(Long userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDTO(savedCart);
    }

    @Transactional
    public CartDTO updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);

        // We might want to check stock again here
        ApiResponse<ProductDTO> productResponse = productClient.getProductById(productId);
        if (productResponse == null || !productResponse.isSuccess() || productResponse.getData() == null) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }
        ProductDTO product = productResponse.getData();

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));

        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Not enough stock for product: " + product.getName());
        }

        item.setQuantity(quantity);
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDTO(savedCart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = Cart.builder().userId(userId).build();
                    return cartRepository.save(cart);
                });
    }
}
