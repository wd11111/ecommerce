package com.wdreslerski.ecommerce.shipping;

import com.wdreslerski.ecommerce.order.Order;
import com.wdreslerski.ecommerce.order.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock
    private ShippingRepository shippingRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ShippingService shippingService;

    private Order order;
    private ShippingAddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        order = Order.builder().id(1L).build();
        addressDTO = new ShippingAddressDTO("Main St", "City", "12345", "Country");
    }

    @Test
    void createShippingInfo_ShouldSave_WhenNoDuplicate() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(shippingRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        // Act
        shippingService.createShippingInfo(1L, addressDTO);

        // Assert
        verify(shippingRepository).save(any(ShippingInfo.class));
    }

    @Test
    void createShippingInfo_ShouldThrowException_WhenDuplicate() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(shippingRepository.findByOrderId(1L)).thenReturn(Optional.of(new ShippingInfo()));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> shippingService.createShippingInfo(1L, addressDTO));
    }

    @Test
    void updateShippingStatus_ShouldUpdateStatus() {
        // Arrange
        ShippingInfo info = new ShippingInfo();
        info.setStatus(ShippingStatus.PENDING);
        when(shippingRepository.findByOrderId(1L)).thenReturn(Optional.of(info));

        // Act
        shippingService.updateShippingStatus(1L, "SHIPPED");

        // Assert
        verify(shippingRepository).save(info);
    }
}
