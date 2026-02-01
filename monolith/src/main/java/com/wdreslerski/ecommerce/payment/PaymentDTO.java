package com.wdreslerski.ecommerce.payment;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionRef;
}
