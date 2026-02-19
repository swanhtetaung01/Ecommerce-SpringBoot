package com.ecommerce.project.service;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //Find an existing cart or create one
        Cart userCart = cartRepository.
        if(userCart == null) {
            Cart newCart = new Cart();
            cartRepository.save(newCart);
        }else {
            userCart = cartRepository.findAll();
        }
        //Retrieve Product Details
        //Perform validations about the product
        //Create a cart item
        //save cart item into the cart
        //return the updated cart
        return null;
    }
}
