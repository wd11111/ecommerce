package com.wdreslerski.cart.mapper;

import com.wdreslerski.cart.dto.CartDTO;
import com.wdreslerski.cart.dto.CartItemDTO;
import com.wdreslerski.cart.model.Cart;
import com.wdreslerski.cart.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartDTO toDTO(Cart cart);

    @Mapping(target = "productId", source = "productId")
    CartItemDTO toDTO(CartItem cartItem);
}
