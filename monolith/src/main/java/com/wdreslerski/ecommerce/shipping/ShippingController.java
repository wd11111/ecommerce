package com.wdreslerski.ecommerce.shipping;

import com.wdreslerski.ecommerce.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @PostMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> createShippingInfo(@PathVariable Long orderId,
            @Valid @RequestBody ShippingAddressDTO addressDTO) {
        shippingService.createShippingInfo(orderId, addressDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shipping info added successfully", null));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateShippingStatus(@PathVariable Long orderId,
            @RequestParam String status) {
        shippingService.updateShippingStatus(orderId, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Shipping status updated successfully", null));
    }
}
