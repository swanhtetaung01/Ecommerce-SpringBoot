package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //Find an existing cart or create one
        Cart cart = getCart();
        //Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        //Perform validations about the product
        if(product.getQuantity() == 0){
            throw new APIException(product.getProductName() + " is out of stock");
        }else if(product.getQuantity() < quantity && product.getQuantity() == 1) {
            throw new APIException("Only 1 item left in the stock");
        }else if(product.getQuantity() < quantity){
            throw new APIException("Only " + product.getQuantity() + " items left in the stock");
        }
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                productId,
                cart.getCartId()
                );
        if(cartItem != null && product.getQuantity() != 0){
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setQuantity(quantity);
        cartItem.setProduct(product);
        cartItem.setProductPrice(product.getPrice());
        cartItem.setDiscount(product.getDiscount());
        cartItem.setCart(cart);
        //Create a cartItem
        //save cart item into the cart
        cartItemRepository.save(cartItem);
        product.setQuantity(product.getQuantity());
        //return the updated cart
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);
        return null;
    }
    private Cart getCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null)
            return userCart;
        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);
    }
}
