package com.es.secondhand.repository;

import com.es.secondhand.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    List<Category> findAllByOrderBySortOrderAsc();
}
