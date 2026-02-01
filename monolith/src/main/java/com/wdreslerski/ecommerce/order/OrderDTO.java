package com.wdreslerski.ecommerce.order;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> items;
    private LocalDateTime createdAt;
}
