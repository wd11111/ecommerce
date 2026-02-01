package com.wdreslerski.ecommerce.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    OrderDTO toDTO(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDTO toDTO(OrderItem orderItem);
}
