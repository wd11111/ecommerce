package com.wdreslerski.ecommerce.payment;

import com.wdreslerski.ecommerce.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> procesPayment(@PathVariable Long orderId) {
        PaymentDTO payment = paymentService.processPayment(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment processed successfully", payment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment details fetched", payment));
    }
}
