package com.edututor.service;

import com.edututor.dto.category.CategoryRequest;
import com.edututor.dto.category.CategoryResponse;
import com.edututor.entity.Category;
import com.edututor.exception.ResourceNotFoundException;
import com.edututor.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    public CategoryResponse create(CategoryRequest request, Long creatorId) {
        Category c = new Category();
        c.setName(request.getName());
        c.setDescription(request.getDescription());
        c.setIcon(request.getIcon());
        c.setCreatedBy(creatorId);
        return CategoryResponse.from(categoryRepository.save(c));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        c.setName(request.getName());
        c.setDescription(request.getDescription());
        c.setIcon(request.getIcon());
        return CategoryResponse.from(categoryRepository.save(c));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
