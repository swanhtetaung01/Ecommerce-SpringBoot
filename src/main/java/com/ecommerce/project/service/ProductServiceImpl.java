package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.jspecify.annotations.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private PaginationService paginationService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));
        boolean ifProductNotPresent = true;
        List<Product> products = category.getProducts();
        for(Product value : products){
            if(value.getProductName().equals(productDTO.getProductName())) {
                ifProductNotPresent = false;
                break;
            }
        }
        if(ifProductNotPresent) {
            Product product = modelMapper.map(productDTO, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() -
                    ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDTO.class);
        }else {
            throw new APIException("Product with name: " + productDTO.getProductName() + " already exists");
        }
    }

//    @Override
//    @Cacheable(
//            value = "PRODUCTS",
//            key = "#pageNumber + '_' + #pageSize + '_' + #sortBy + '_' + #sortOrder"
//    )
//    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize,
//                                          String sortBy, String sortOrder) {
//
//        System.out.println("Fetching from DB (NOT CACHE)");
//
//        Pageable pageDetails = paginationService.getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
//        Page<Product> productPage = productRepository.findAll(pageDetails);
//        List<Product> products = productPage.getContent();
//        if(products.isEmpty())
//            throw new APIException("No product added yet");
//        return getProductResponse(pageNumber, pageSize, productPage, products);
//    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if(products.isEmpty())
            throw new APIException("No product added yet");
        return products.stream().toList();
    }

    @Override
    public ProductResponse getProductsByCategory(Integer pageNumber, Integer pageSize,
                                                 String sortBy, String sortOrder,
                                                 Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Pageable pageDetails = paginationService.getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty())
            throw new APIException("No product added under category name: "+ category.getCategoryName());
        return getProductResponse(pageNumber, pageSize, productPage, products);
    }

    @Override
    public ProductResponse getProductsByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sortDetails = sortBy.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        Pageable pageDetails = paginationService.getPageDetails(pageNumber, pageSize, sortBy, sortOrder);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
        List<Product> products = productPage.getContent();
        if(products.isEmpty())
            throw new APIException("No product found under keyword: " + keyword);
        return getProductResponse(pageNumber, pageSize, productPage, products);
    }

    @NonNull
    private ProductResponse getProductResponse(Integer pageNumber, Integer pageSize, Page<Product> productPage, List<Product> products) {
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product targetProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        Product product = modelMapper.map(productDTO, Product.class);
        targetProduct.setProductName(product.getProductName());
        targetProduct.setDescription(product.getDescription());
        targetProduct.setQuantity(product.getQuantity());
        targetProduct.setPrice(product.getPrice());
        targetProduct.setDiscount(product.getDiscount());
        Double specialPrice = product.getPrice() - (product.getPrice() * (product.getDiscount() * 0.01));
        targetProduct.setSpecialPrice(specialPrice);
        Product updatedProduct = productRepository.save(targetProduct);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> productDTOs = cart.getCartItems().stream().map(p ->
                    modelMapper.map(p.getProduct(), ProductDTO.class)).toList();
            cartDTO.setProducts(productDTOs);
            return cartDTO;
        }).toList();
        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override                                              
    public String deleteProduct(Long productId) {
        Product targetProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.deleteById(productId);
        return "Product is deleted successfully";
    }

    @Override
    public ProductDTO updateProductImage(MultipartFile image, Long productId) throws IOException {
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        String fileName = fileService.uploadImage(path, image);
        productFromDb.setImage(fileName);
        Product updatedProduct = productRepository.save(productFromDb);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }
}
