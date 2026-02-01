package com.wdreslerski.ecommerce.cart;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDTO toDTO(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDTO toDTO(CartItem cartItem);
}
