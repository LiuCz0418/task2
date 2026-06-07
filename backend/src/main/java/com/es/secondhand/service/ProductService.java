package com.es.secondhand.service;

import com.es.secondhand.dto.ProductDTO;
import com.es.secondhand.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    
    List<Category> getAllCategories();
    
    Page<ProductDTO.ProductListItem> getProducts(Integer categoryId, String keyword, Pageable pageable);
    
    ProductDTO.ProductDetail getProductDetail(Integer productId);
    
    ProductDTO.ProductDetail createProduct(Integer sellerId, ProductDTO.CreateRequest request);
    
    ProductDTO.ProductDetail updateProduct(Integer productId, Integer sellerId, ProductDTO.UpdateRequest request);
    
    void deleteProduct(Integer productId, Integer sellerId);
    
    void batchDeleteProducts(List<Integer> productIds, Integer sellerId);
    
    Page<ProductDTO.ProductListItem> getMyProducts(Integer sellerId, Integer status, Pageable pageable);
    
    List<ProductDTO.ProductListItem> getHotProducts();
    
    List<ProductDTO.ProductListItem> getLatestProducts();
    
    // 收藏相关
    void addFavorite(Integer userId, Integer productId);
    
    void removeFavorite(Integer userId, Integer productId);
    
    boolean isFavorite(Integer userId, Integer productId);
    
    Page<ProductDTO.ProductListItem> getFavorites(Integer userId, Pageable pageable);
}
