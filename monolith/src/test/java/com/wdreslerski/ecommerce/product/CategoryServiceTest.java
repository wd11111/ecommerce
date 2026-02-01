package com.wdreslerski.ecommerce.product;

import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Electronics").build();
        categoryDTO = new CategoryDTO();
        categoryDTO.setId(1L);
        categoryDTO.setName("Electronics");
    }

    @Test
    void createCategory_ShouldSave_WhenNoParent() {
        // Arrange
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.createCategory(categoryDTO);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(category);
    }

    @Test
    void getCategoryById_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act
        CategoryDTO result = categoryService.getCategoryById(1L);

        // Assert
        assertEquals("Electronics", result.getName());
    }
}
