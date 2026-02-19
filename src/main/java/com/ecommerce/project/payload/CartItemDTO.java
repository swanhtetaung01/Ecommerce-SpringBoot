package com.ecommerce.project.payload;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Product;

public class CartItemDTO {
    private Long cartItemId;
    private CartDTO cart;
    private ProductDTO product;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
