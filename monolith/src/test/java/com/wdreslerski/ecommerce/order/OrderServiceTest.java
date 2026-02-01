package com.wdreslerski.ecommerce.order;

import com.wdreslerski.ecommerce.cart.Cart;
import com.wdreslerski.ecommerce.cart.CartItem;
import com.wdreslerski.ecommerce.cart.CartRepository;
import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.product.Product;
import com.wdreslerski.ecommerce.product.ProductRepository;
import com.wdreslerski.ecommerce.user.User;
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
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.TEN)
                .stockQuantity(10)
                .build();
        cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void createOrder_ShouldCreateOrder_WhenStockIsSufficient() {
        // Arrange
        CartItem cartItem = CartItem.builder()
                .product(product)
                .quantity(2)
                .cart(cart)
                .price(BigDecimal.TEN)
                .build();
        cart.getItems().add(cartItem);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });
        when(orderMapper.toDTO(any(Order.class))).thenReturn(new OrderDTO());

        // Act
        orderService.createOrder(1L);

        // Assert
        verify(productRepository, times(1)).save(product); // Stock updated
        assertEquals(8, product.getStockQuantity());
        assertTrue(cart.getItems().isEmpty()); // Cart cleared
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> orderService.createOrder(1L));
    }
}
