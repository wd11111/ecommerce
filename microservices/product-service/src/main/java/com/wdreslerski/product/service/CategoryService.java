package com.wdreslerski.product.service;

import com.wdreslerski.product.dto.CategoryDTO;
import com.wdreslerski.product.exception.ResourceNotFoundException;
import com.wdreslerski.product.mapper.CategoryMapper;
import com.wdreslerski.product.model.Category;
import com.wdreslerski.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.toDTO(category);
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDTO.getParentId()));
            category.setParent(parent);
        }
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }
}
