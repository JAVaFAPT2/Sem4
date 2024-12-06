package com.project.shopapp.services.category;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.CategoryNotFoundException;
import com.project.shopapp.exceptions.CategoryDeletionException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

  @Override
  @Transactional
  public Category createCategory(CategoryDTO categoryDTO) {
    Category newCategory = Category.builder()
            .name(categoryDTO.getName())
            .build();
    logger.info("Creating new category with name: {}", categoryDTO.getName());
    return categoryRepository.save(newCategory);
  }

  @Override
  public Category getCategoryById(long id) throws CategoryNotFoundException {
    return categoryRepository.findById(id)
            .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
  }

  @Override
  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }

  @Override
  @Transactional
  public Category updateCategory(long categoryId, CategoryDTO categoryDTO) throws CategoryNotFoundException {
    Category existingCategory = getCategoryById(categoryId);
    existingCategory.setName(categoryDTO.getName());
    logger.info("Updating category with id: {}, new name: {}", categoryId, categoryDTO.getName());
    return categoryRepository.save(existingCategory);
  }

  @Override
  @Transactional
  public Category deleteCategory(long id) throws CategoryNotFoundException, CategoryDeletionException {
    Category category = getCategoryById(id);

    List<Product> products = productRepository.findByCategory(category);
    if (!products.isEmpty()) {
      logger.error("Attempted to delete category with id: {} which has associated products.", id);
      throw new CategoryDeletionException("Cannot delete category with associated products");
    }

    categoryRepository.deleteById(id);
    logger.info("Deleted category with id: {}", id);
    return category;
  }
}