package com.inventory.repository;

import com.inventory.entity.Product;
import com.inventory.entity.enums.StockStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    long countByStatus(StockStatus status);

    @Query("SELECT COALESCE(SUM(p.price * p.quantity), 0) FROM Product p")
    BigDecimal getTotalInventoryValue();

    List<Product> findByStatusOrderByQuantityAsc(StockStatus status, Pageable pageable);

    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findDistinctCategories();

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);
}
