package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Integer categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Integer categoryId);

    ProductResponse getProductsByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword);

    ProductDTO updateProduct(ProductDTO productDTO, Integer productId);

    String deleteProduct(Integer productId);

    ProductDTO updateProductImage(MultipartFile image, Integer productId) throws IOException;

}
