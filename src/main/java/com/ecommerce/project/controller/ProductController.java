package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Product APIs", description = "APIs for managing products")
public class ProductController {
    @Autowired
    ProductService productService;

    @Operation(summary = "Add a product", description = "API for adding a new product")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId){
        return new ResponseEntity<>(productService.addProduct(categoryId, productDTO), HttpStatus.CREATED);
    }

    

//    @Operation(summary = "Get all products")
//    @GetMapping("/public/products")
//    public ResponseEntity<ProductResponse> getAllProducts(
//            @RequestParam (name = "pageNubmer", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
//            @RequestParam (name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
//            @RequestParam (name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
//            @RequestParam (name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
//    ) {
//        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);
//        return new ResponseEntity<>(productResponse, HttpStatus.OK);
//    }

    @Operation(summary = "Get all products")
    @GetMapping("/public/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by category", description = "API for fetching products from a desired category")
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
            @RequestParam (name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam (name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam (name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam (name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder,
            @PathVariable Long categoryId) {
        return new ResponseEntity<>(productService.getProductsByCategory(pageNumber, pageSize, sortBy, sortOrder, categoryId), HttpStatus.OK);
    }

    @Operation(summary = "Get products by keyword", description = "API for fetching products with keyword filtering")
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(
            @RequestParam (name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam (name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam (name = "sortBy", defaultValue = AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
            @RequestParam (name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder,
            @PathVariable String keyword) {
        return new ResponseEntity<>(productService.getProductsByKeyword(pageNumber, pageSize, sortBy, sortOrder, keyword), HttpStatus.OK);
    }

    @Operation(summary = "Update a product", description = "API for updating a product")
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId) {
        return new ResponseEntity<>(productService.updateProduct(productDTO, productId), HttpStatus.OK);
    }

    @Operation(summary = "Delete a product", description = "API for deleting a product")
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.deleteProduct(productId), HttpStatus.OK);
    }

    @Operation(summary = "Update product image", description = "API for updating an image attached to a product")
    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@RequestParam("image")MultipartFile image,
                                                         @PathVariable Long productId) throws IOException {
        return new ResponseEntity<>(productService.updateProductImage(image, productId), HttpStatus.OK);
    }
}
