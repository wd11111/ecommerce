package com.wdreslerski.ecommerce.analytics;

import com.wdreslerski.ecommerce.order.Order;
import com.wdreslerski.ecommerce.order.OrderItemRepository;
import com.wdreslerski.ecommerce.order.OrderRepository;
import com.wdreslerski.ecommerce.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getAnalytics_ShouldReturnData() {
        // Arrange
        when(orderRepository.count()).thenReturn(10L);

        Order order1 = Order.builder().status(OrderStatus.PAID).totalPrice(BigDecimal.valueOf(100)).build();
        Order order2 = Order.builder().status(OrderStatus.NEW).totalPrice(BigDecimal.valueOf(50)).build();
        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        List<Object[]> bestSelling = new ArrayList<>();
        bestSelling.add(new Object[] { 1L, "Product A", 5L });
        when(orderItemRepository.findBestSellingProducts(any(Pageable.class))).thenReturn(bestSelling);

        // Act
        Map<String, Object> result = analyticsService.getAnalytics();

        // Assert
        assertEquals(10L, result.get("totalOrders"));
        assertEquals(BigDecimal.valueOf(100), result.get("totalRevenue"));
        assertNotNull(result.get("bestSellingProducts"));
    }
}
