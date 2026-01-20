package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null)
            throw new APIException("Category with name:"
                    + savedCategory.getCategoryName() + " already exists");
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
         Category targetCategory = categoryRepository.findById(categoryId)
                 .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
         categoryRepository.delete(targetCategory);
        return "Category with categoryId " + categoryId + " is deleted successfully";
    }

    @Override
    public String updateCategory(Long categoryId, Category category) {
        Category updatedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        updatedCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(updatedCategory);
        return "Category updated successfully";
    }
}
