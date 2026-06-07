package com.es.secondhand.repository;

import com.es.secondhand.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    Page<Product> findByStatus(Integer status, Pageable pageable);
    
    Page<Product> findByCategoryIdAndStatus(Integer categoryId, Integer status, Pageable pageable);
    
    Page<Product> findBySellerIdAndStatus(Integer sellerId, Integer status, Pageable pageable);
    
    Page<Product> findBySellerId(Integer sellerId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = 1 AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Modifying
    @Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Integer id);
    
    List<Product> findTop10ByStatusOrderByViewCountDesc(Integer status);
    
    List<Product> findTop10ByStatusOrderByCreatedAtDesc(Integer status);
}
