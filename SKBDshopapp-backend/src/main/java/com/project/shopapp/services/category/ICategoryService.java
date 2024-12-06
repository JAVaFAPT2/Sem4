package com.project.shopapp.services.category;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exceptions.CategoryDeletionException;
import com.project.shopapp.exceptions.CategoryNotFoundException;
import com.project.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
  Category createCategory(CategoryDTO category);

  Category getCategoryById(long id) throws CategoryNotFoundException;

  List<Category> getAllCategories();

  Category updateCategory(long categoryId, CategoryDTO category) throws CategoryNotFoundException;

  Category deleteCategory(long id) throws Exception, CategoryDeletionException;
}
