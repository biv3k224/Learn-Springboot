package com.example.product_catalog_api.service;

import com.example.product_catalog_api.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    //create
    Product createProduct(Product product);

    //Read
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    List<Product> getProductsByCategory(String category);
    // Add this method to your ProductService interface
    List<Product> searchProductsByName(String name);

    //Update
    Product updateProduct(Long id, Product productDetails);

    //Delete
    void deleteProduct(Long id);

    //Business Logic
    boolean existsById(Long id);
    long countProducts();

    BigDecimal calculateInventoryValue();

    long getCategoryCount();


}
