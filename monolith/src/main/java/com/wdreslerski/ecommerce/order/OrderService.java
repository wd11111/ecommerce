package com.wdreslerski.ecommerce.order;

import com.wdreslerski.ecommerce.cart.Cart;
import com.wdreslerski.ecommerce.cart.CartItem;
import com.wdreslerski.ecommerce.cart.CartRepository;
import com.wdreslerski.ecommerce.product.Product;
import com.wdreslerski.ecommerce.product.ProductRepository;
import com.wdreslerski.ecommerce.exception.BadRequestException;
import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
import com.wdreslerski.ecommerce.user.User;
import com.wdreslerski.ecommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO createOrder(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user", "id", userId));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        Order order = Order.builder()
                .user(cart.getUser())
                .status(OrderStatus.NEW)
                .totalPrice(cart.getTotalPrice())
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return orderMapper.toDTO(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return orderMapper.toDTO(order);
    }

    @Transactional
    public OrderDTO updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }
}
