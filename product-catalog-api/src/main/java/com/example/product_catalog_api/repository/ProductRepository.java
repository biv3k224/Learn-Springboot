package com.example.product_catalog_api.repository;

import com.example.product_catalog_api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    @Query("SELECT p FROM Product p WHERE p.category = :cat ORDER BY p.price DESC")
    List<Product> findProductsByCategorySortedByPrice(@Param("cat") String category);

    List<Product> findByPriceLessThan(BigDecimal price);

    List<Product> findByNameContainingIgnoreCase(String name);

}
