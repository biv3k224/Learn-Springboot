package com.example.product_catalog_api.controller;

import com.example.product_catalog_api.entity.Product;
import com.example.product_catalog_api.repository.ProductRepository;
import com.example.product_catalog_api.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {

    @Autowired
    private ProductService productService;

    // CREATE - POST /api/products
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // READ ALL - GET /api/products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // READ ONE - GET /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Product not found with id: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // READ BY CATEGORY - GET /api/products/category/{category}
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    // UPDATE - PUT /api/products/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // DELETE - DELETE /api/products/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    // GET COUNT - GET /api/products/count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countProducts() {
        long count = productService.countProducts();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    // CHECK EXISTS - GET /api/products/{id}/exists
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> productExists(@PathVariable Long id) {
        boolean exists = productService.existsById(id);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    // GET INVENTORY VALUE - GET /api/products/inventory/value
    @GetMapping("/inventory/value")
    public ResponseEntity<Map<String, BigDecimal>> getInventoryValue() {
        try {
            BigDecimal value = productService.calculateInventoryValue();
            Map<String, BigDecimal> response = new HashMap<>();
            response.put("inventoryValue", value);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, BigDecimal> error = new HashMap<>();
            error.put("inventoryValue", BigDecimal.ZERO);
            return ResponseEntity.ok(error);
        }
    }

    // SEARCH - GET /api/products/search?name={name}
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    // GET CATEGORY COUNT - GET /api/products/categories/count
    @GetMapping("/categories/count")
    public ResponseEntity<Map<String, Long>> getCategoryCount() {
        try {
            long count = productService.getCategoryCount();
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Long> error = new HashMap<>();
            error.put("count", 0L);
            return ResponseEntity.ok(error);
        }
    }
}