package com.wdreslerski.product.mapper;

import com.wdreslerski.product.dto.CategoryDTO;
import com.wdreslerski.product.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parentId", source = "parent.id")
    CategoryDTO toDTO(Category category);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    Category toEntity(CategoryDTO categoryDTO);
}
