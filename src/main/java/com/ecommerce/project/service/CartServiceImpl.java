package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = getCart();
        //Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

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

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        // Always sync product and pricing details
        cartItem.setProduct(product);
        cartItem.setProductPrice(product.getPrice());
        cartItem.setDiscount(product.getDiscount());

        cartItemRepository.save(cartItem);

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productStream = cartItems.stream().map(item -> {
            ProductDTO map = modelMapper.map(item.getProduct(), ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()) {
            throw new APIException("No cart exist");
        }

        return carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream().map(item ->
                    modelMapper.map(item.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if(cart == null)
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        cart.getCartItems().forEach(c -> c.getProduct().setQuantity(c.getQuantity()));
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        List<ProductDTO> products = cart.getCartItems().stream().map(cartItem ->
            modelMapper.map(cartItem.getProduct(), ProductDTO.class)).toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, int delete) {
        return null;
    }

    private Cart getCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInUserEmail());
        if(userCart != null)
            return userCart;
        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);
    }
}
