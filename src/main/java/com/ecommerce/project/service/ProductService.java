package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId);

    ProductResponse getProductsByKeyword(String keyword);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    String deleteProduct(Long productId);

    ProductDTO updateProductImage(MultipartFile image, Long productId) throws IOException;

}
