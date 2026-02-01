package com.wdreslerski.ecommerce.cart;

import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
import com.wdreslerski.ecommerce.product.Product;
import com.wdreslerski.ecommerce.product.ProductRepository;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Transactional
    public CartDTO getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toDTO(cart);
    }

    @Transactional
    public CartDTO addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException("Not enough stock for product: " + product.getName());
        }

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
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
                    .product(product)
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
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toDTO(savedCart);
    }

    @Transactional
    public CartDTO updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
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
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                    Cart cart = Cart.builder().user(user).build();
                    return cartRepository.save(cart);
                });
    }
}
