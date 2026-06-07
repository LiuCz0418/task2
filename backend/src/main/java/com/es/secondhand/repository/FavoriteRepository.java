package com.es.secondhand.repository;

import com.es.secondhand.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    
    Optional<Favorite> findByUserIdAndProductId(Integer userId, Integer productId);
    
    boolean existsByUserIdAndProductId(Integer userId, Integer productId);
    
    void deleteByUserIdAndProductId(Integer userId, Integer productId);
    
    Page<Favorite> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    long countByUserId(Integer userId);
}
