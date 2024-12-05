package com.example.beskbd.services;

import com.example.beskbd.dto.object.CategoryDto;
import com.example.beskbd.dto.request.CategoryCreationRequest;
import com.example.beskbd.dto.response.CategoryResponse;
import com.example.beskbd.entities.Category;
import com.example.beskbd.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;


    public CategoryResponse createNewCategory(CategoryCreationRequest request) {
        try {
            Category.Gender gender = Category.Gender.valueOf(request.getGender().toString().toUpperCase());
            Category category = Category.builder()
                    .gender(gender)
                    .description(request.getCategoryDescription())
                    .name(request.getCategoryName())
                    .productType(request.getProductType())
                    .build();
            category = categoryRepository.save(category); // Save category and return

            return convertToResponse(category); // Convert and return as response
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid gender value: " + request.getGender());
        }
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToResponse) // Mapping entity to DTO
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        return convertToResponse(category); // Return DTO
    }

    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }

    // Utility method to convert Category entity to CategoryResponse DTO
    private CategoryResponse convertToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId()) // assuming 'id' exists in your BaseEntity
                .name(category.getName())
                .description(category.getDescription())
                .gender(category.getGender().name()) // using name() to convert enum to String
                .productType(category.getProductType())
                .products(category.getProducts()) // Optionally return products
                .build();
    }

    public CategoryCreationRequest createCategory(CategoryCreationRequest categoryDTO) {
        logger.info("Creating new category: {}", categoryDTO);

        // Input validation (optional, adjust based on your requirements)
        if (categoryDTO.getCategoryName() == null || categoryDTO.getCategoryName().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        // Map DTO to Entity
        Category category = new Category();
        category.setName(categoryDTO.getCategoryName());
        // Set other properties accordingly
        if (categoryDTO.getCategoryDescription() != null) {
            category.setDescription(categoryDTO.getCategoryDescription());
        }

        // Persist the category entity
        Category savedCategory = categoryRepository.save(category);

        // Optionally, map back the saved entity to DTO if needed
        // For simplicity, assuming you want to return the DTO back
        return new CategoryCreationRequest(savedCategory.getId(), savedCategory.getName(),savedCategory.getGender(), savedCategory.getDescription(),savedCategory.getProductType());
    }

    public CategoryDto updateCategory(CategoryCreationRequest categoryRequest) {
        logger.info("Updating category with ID: {}", categoryRequest.getId());

        // Find the existing category
        Category category = categoryRepository.findById(categoryRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryRequest.getId()));

        // Update category fields
        category.setName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getCategoryDescription());

        // Use Gender enum safely, handle possible IllegalArgumentException for invalid gender
        try {
            category.setGender(Category.Gender.valueOf(categoryRequest.getGender().toString().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value: " + categoryRequest.getGender());
        }
        category.setProductType(categoryRequest.getProductType());
        Category updatedCategory = categoryRepository.save(category);
        return getCategoryDto(updatedCategory);
    }

    private static CategoryDto getCategoryDto(Category updatedCategory) {
        CategoryDto updatedCategoryDto = new CategoryDto();
        updatedCategoryDto.setId(updatedCategory.getId());
        updatedCategoryDto.setCategoryName(updatedCategory.getName());
        updatedCategoryDto.setCategoryDescription(updatedCategory.getDescription());
        updatedCategoryDto.setGender(updatedCategory.getGender());
        updatedCategoryDto.setProductType(updatedCategory.getProductType());
        return updatedCategoryDto;
    }
}