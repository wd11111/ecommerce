package com.wdreslerski.ecommerce.shipping;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingRepository shippingRepository;

    private final com.wdreslerski.ecommerce.order.OrderRepository orderRepository;

    @Transactional
    public void createShippingInfo(Long orderId, ShippingAddressDTO addressDTO) {
        com.wdreslerski.ecommerce.order.Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new com.wdreslerski.ecommerce.exception.ResourceNotFoundException("Order", "id",
                        orderId));
        createShippingInfo(order, addressDTO);
    }

    @Transactional
    public void createShippingInfo(com.wdreslerski.ecommerce.order.Order order, ShippingAddressDTO addressDTO) {
        if (shippingRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException("Shipping info already exists for this order");
        }

        ShippingInfo shippingInfo = ShippingInfo.builder()
                .order(order)
                .address(addressDTO.address())
                .city(addressDTO.city())
                .postalCode(addressDTO.postalCode())
                .country(addressDTO.country())
                .status(ShippingStatus.PENDING)
                .build();

        shippingRepository.save(shippingInfo);
    }

    @Transactional
    public void updateShippingStatus(Long orderId, String status) {
        ShippingInfo shippingInfo = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping info not found for order id: " + orderId));

        shippingInfo.setStatus(ShippingStatus.valueOf(status.toUpperCase()));
        shippingRepository.save(shippingInfo);
    }
}
