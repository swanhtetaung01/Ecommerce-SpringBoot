package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private List<Category> categories = new ArrayList<>();
    //private Long nextId = 1L;

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
//        category.setCategoryId(nextId++);
//        categories.add(category);
    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Category with categoryId "
                                + categoryId + " is not found"));

        categories.remove(category);
        return "Category with categoryId " + categoryId + " is deleted successfully";
    }

    @Override
    public String updateCategory(Long categoryId, Category category) {
        Optional<Category> optionalCategory = categories.stream()
                .filter(c -> c.getCategoryId().equals(categoryId))
                .findFirst();

        if (optionalCategory.isPresent()) {
            optionalCategory.get().setCategoryName(category.getCategoryName());
            return "Category updated successfully";
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category does not exist");
        }

    }
}
