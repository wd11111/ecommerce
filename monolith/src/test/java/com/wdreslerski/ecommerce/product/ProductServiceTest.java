package com.wdreslerski.ecommerce.product;

import com.wdreslerski.ecommerce.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(BigDecimal.valueOf(100))
                .build();

        productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setName("Test Product");
        productDTO.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
    }

    @Test
    void getProductById_ShouldThrowException_WhenNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct_ShouldSaveProduct() {
        // Arrange
        when(productMapper.toEntity(productDTO)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        ProductDTO result = productService.createProduct(productDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository).save(product);
    }

    @Test
    void searchProducts_ShouldReturnPage() {
        // Arrange
        Page<Product> page = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        // Act
        Page<ProductDTO> result = productService.searchProducts("test", null, null, null, null, Pageable.unpaged());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
