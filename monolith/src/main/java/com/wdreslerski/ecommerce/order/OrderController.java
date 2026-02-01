package com.wdreslerski.ecommerce.order;

import com.wdreslerski.ecommerce.common.ApiResponse;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        OrderDTO order = orderService.createOrder(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order created successfully", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getUserOrders(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        List<OrderDTO> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully", orders));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderDTO order = orderService.getOrderById(id);

        Long userId = getUserId(userDetails);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to view this order");
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Order fetched successfully", order));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        OrderDTO order = orderService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated", order));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
