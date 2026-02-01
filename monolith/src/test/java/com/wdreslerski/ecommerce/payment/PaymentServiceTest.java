package com.wdreslerski.ecommerce.payment;

import com.wdreslerski.ecommerce.order.Order;
import com.wdreslerski.ecommerce.order.OrderRepository;
import com.wdreslerski.ecommerce.order.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .status(OrderStatus.NEW)
                .totalPrice(BigDecimal.TEN)
                .build();
    }

    @Test
    void processPayment_ShouldSucceed_WhenOrderIsNew() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId(1L);
            return p;
        });

        // Act
        PaymentDTO result = paymentService.processPayment(1L);

        // Assert
        assertNotNull(result);
        assertEquals(PaymentStatus.SUCCESS, result.getStatus());
        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void processPayment_ShouldThrowException_WhenOrderAlreadyPaid() {
        // Arrange
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> paymentService.processPayment(1L));
        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
