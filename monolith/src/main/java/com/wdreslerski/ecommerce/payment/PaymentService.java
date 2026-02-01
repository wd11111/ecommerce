package com.wdreslerski.ecommerce.payment;

import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
import com.wdreslerski.ecommerce.order.Order;
import com.wdreslerski.ecommerce.order.OrderRepository;
import com.wdreslerski.ecommerce.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentDTO processPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Order is already processed or cancelled");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalPrice())
                .status(PaymentStatus.SUCCESS)
                .build();
        payment.generateTransactionRef();

        Payment savedPayment = paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return toDTO(savedPayment);
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));
        return toDTO(payment);
    }

    private PaymentDTO toDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus());
        dto.setTransactionRef(payment.getTransactionRef());
        return dto;
    }
}
