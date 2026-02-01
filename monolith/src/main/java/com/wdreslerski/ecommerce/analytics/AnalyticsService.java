package com.wdreslerski.ecommerce.analytics;

import com.wdreslerski.ecommerce.order.Order;
import com.wdreslerski.ecommerce.order.OrderItemRepository;
import com.wdreslerski.ecommerce.order.OrderRepository;
import com.wdreslerski.ecommerce.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> getAnalytics() {
        Map<String, Object> data = new HashMap<>();

        long totalOrders = orderRepository.count();
        data.put("totalOrders", totalOrders);

        BigDecimal totalRevenue = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.SHIPPED
                        || o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("totalRevenue", totalRevenue);

        List<Object[]> bestSelling = orderItemRepository.findBestSellingProducts(PageRequest.of(0, 5));
        List<Map<String, Object>> bestSellingList = new ArrayList<>();
        for (Object[] row : bestSelling) {
            Map<String, Object> productData = new HashMap<>();
            productData.put("productId", row[0]);
            productData.put("productName", row[1]);
            productData.put("totalSold", row[2]);
            bestSellingList.add(productData);
        }
        data.put("bestSellingProducts", bestSellingList);

        return data;
    }
}
