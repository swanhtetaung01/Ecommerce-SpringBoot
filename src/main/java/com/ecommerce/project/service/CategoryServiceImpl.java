package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<>();

    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategory(Category category) {
        if (categories.isEmpty()) {
            category.setCategoryId(1L);
            categories.add(category);
        }else {
            category.setCategoryId(categories.getLast().getCategoryId() + 1);
            categories.add(category);
        }
    }
}
