package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void createCategory(Category category) {
        category.setCategoryId(null);
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
         Category targetCategory = categoryRepository.findById(categoryId)
                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND , "Category does not exist"));
         categoryRepository.delete(targetCategory);
        return "Category with categoryId " + categoryId + " is deleted successfully";
    }

    @Override
    public String updateCategory(Long categoryId, Category category) {
        Category updatedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist"));
        updatedCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(updatedCategory);
        return "Category updated successfully";
    }
}
