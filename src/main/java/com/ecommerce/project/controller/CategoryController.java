package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private List<Category> categories = new ArrayList<>();

    @GetMapping("/public/categories")
    public List<Category> getAllCategories() {
        return categories;
    }

    @PostMapping("/public/categories")
    public String createCategory(@RequestBody Category category) {
        if (category != null) return "Category addition successful";
        else return "Category addition unsuccessful";
    }

}
