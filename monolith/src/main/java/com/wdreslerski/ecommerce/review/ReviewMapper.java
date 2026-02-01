package com.wdreslerski.ecommerce.review;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "userName", expression = "java(review.getUser().getFirstName() + ' ' + review.getUser().getLastName())")
    ReviewDTO toDTO(Review review);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Review toEntity(ReviewDTO reviewDTO);
}
