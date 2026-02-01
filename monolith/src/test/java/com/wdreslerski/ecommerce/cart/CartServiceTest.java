package com.wdreslerski.ecommerce.cart;

import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();
        cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
        product = Product.builder().id(1L).name("Test Product").stockQuantity(10).price(BigDecimal.TEN).build();
    }

    @Test
    void addToCart_ShouldAddItem_WhenStockIsSufficient() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toDTO(cart)).thenReturn(new CartDTO());

        // Act
        cartService.addToCart(1L, 1L, 2);

        // Assert
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
        verify(cartRepository).save(cart);
    }

    @Test
    void addToCart_ShouldThrowException_WhenStockInsufficient() {
        // Arrange
        product.setStockQuantity(1);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> cartService.addToCart(1L, 1L, 2));
    }

    @Test
    void removeItem_ShouldRemoveItem() {
        // Arrange
        CartItem item = CartItem.builder().product(product).cart(cart).build();
        cart.getItems().add(item);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toDTO(cart)).thenReturn(new CartDTO());

        // Act
        cartService.removeItem(1L, 1L);

        // Assert
        assertTrue(cart.getItems().isEmpty());
    }
}
