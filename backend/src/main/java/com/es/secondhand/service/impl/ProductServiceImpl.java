package com.es.secondhand.service.impl;

import com.es.secondhand.dto.ProductDTO;
import com.es.secondhand.entity.Category;
import com.es.secondhand.entity.Favorite;
import com.es.secondhand.entity.Product;
import com.es.secondhand.entity.ProductImage;
import com.es.secondhand.entity.User;
import com.es.secondhand.repository.CategoryRepository;
import com.es.secondhand.repository.FavoriteRepository;
import com.es.secondhand.repository.ProductImageRepository;
import com.es.secondhand.repository.ProductRepository;
import com.es.secondhand.repository.ReviewRepository;
import com.es.secondhand.repository.UserRepository;
import com.es.secondhand.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository productImageRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }
    
    @Override
    public Page<ProductDTO.ProductListItem> getProducts(Integer categoryId, String keyword, Pageable pageable) {
        Page<Product> products;
        
        if (StringUtils.hasText(keyword)) {
            products = productRepository.searchByKeyword(keyword, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByCategoryIdAndStatus(categoryId, 1, pageable);
        } else {
            products = productRepository.findByStatus(1, pageable);
        }
        
        return products.map(this::convertToListItem);
    }
    
    @Override
    @Transactional
    public ProductDTO.ProductDetail getProductDetail(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        productRepository.incrementViewCount(productId);
        
        return convertToDetail(product);
    }
    
    @Override
    @Transactional
    public ProductDTO.ProductDetail createProduct(Integer sellerId, ProductDTO.CreateRequest request) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSeller(seller);
        product.setLocation(request.getLocation());
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            product.setCategory(category);
        }
        
        if (request.getConditionLevel() != null) {
            product.setConditionLevel(request.getConditionLevel());
        }
        
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImageUrl(request.getImages().get(0));
        }
        
        product = productRepository.save(product);
        
        if (request.getImages() != null) {
            final Product savedProduct = product;
            int order = 0;
            for (String imageUrl : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                image.setImageUrl(imageUrl);
                image.setSortOrder(order++);
                productImageRepository.save(image);
            }
        }
        
        return convertToDetail(product);
    }
    
    @Override
    @Transactional
    public ProductDTO.ProductDetail updateProduct(Integer productId, Integer sellerId, ProductDTO.UpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权修改此商品");
        }
        
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getOriginalPrice() != null) {
            product.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            product.setCategory(category);
        }
        if (request.getConditionLevel() != null) {
            product.setConditionLevel(request.getConditionLevel());
        }
        if (request.getLocation() != null) {
            product.setLocation(request.getLocation());
        }
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }
        
        if (request.getImages() != null) {
            productImageRepository.deleteByProductId(productId);
            product.setImageUrl(request.getImages().isEmpty() ? null : request.getImages().get(0));
            
            int order = 0;
            for (String imageUrl : request.getImages()) {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(imageUrl);
                image.setSortOrder(order++);
                productImageRepository.save(image);
            }
        }
        
        product = productRepository.save(product);
        return convertToDetail(product);
    }
    
    @Override
    @Transactional
    public void deleteProduct(Integer productId, Integer sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (!product.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权删除此商品");
        }
        
        productRepository.delete(product);
    }
    
    @Override
    @Transactional
    public void batchDeleteProducts(List<Integer> productIds, Integer sellerId) {
        if (productIds == null || productIds.isEmpty()) {
            throw new RuntimeException("请选择要删除的商品");
        }
        
        int deletedCount = 0;
        List<String> errors = new java.util.ArrayList<>();
        
        for (Integer productId : productIds) {
            try {
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("商品不存在"));
                
                if (!product.getSeller().getId().equals(sellerId)) {
                    errors.add("商品ID" + productId + "：无权删除");
                    continue;
                }
                
                productRepository.delete(product);
                deletedCount++;
            } catch (Exception e) {
                errors.add("商品ID" + productId + "：" + e.getMessage());
            }
        }
        
        if (deletedCount == 0) {
            throw new RuntimeException("所有商品删除失败：" + String.join("; ", errors));
        }
        
        if (!errors.isEmpty()) {
            throw new RuntimeException("部分删除失败：" + String.join("; ", errors));
        }
    }
    
    @Override
    public Page<ProductDTO.ProductListItem> getMyProducts(Integer sellerId, Integer status, Pageable pageable) {
        Page<Product> products;
        if (status != null) {
            products = productRepository.findBySellerIdAndStatus(sellerId, status, pageable);
        } else {
            products = productRepository.findBySellerId(sellerId, pageable);
        }
        return products.map(this::convertToListItem);
    }
    
    @Override
    public List<ProductDTO.ProductListItem> getHotProducts() {
        return productRepository.findTop10ByStatusOrderByViewCountDesc(1).stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ProductDTO.ProductListItem> getLatestProducts() {
        return productRepository.findTop10ByStatusOrderByCreatedAtDesc(1).stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void addFavorite(Integer userId, Integer productId) {
        if (favoriteRepository.existsByUserIdAndProductId(userId, productId)) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setProductId(productId);
        favoriteRepository.save(favorite);
    }
    
    @Override
    @Transactional
    public void removeFavorite(Integer userId, Integer productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
    
    @Override
    public boolean isFavorite(Integer userId, Integer productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }
    
    @Override
    public Page<ProductDTO.ProductListItem> getFavorites(Integer userId, Pageable pageable) {
        Page<Favorite> favoritePage = favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<ProductDTO.ProductListItem> items = favoritePage.getContent().stream()
                .map(fav -> productRepository.findById(fav.getProductId()).orElse(null))
                .filter(product -> product != null)
                .map(this::convertToListItem)
                .collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(items, pageable, favoritePage.getTotalElements());
    }
    
    private ProductDTO.ProductListItem convertToListItem(Product product) {
        ProductDTO.ProductListItem item = new ProductDTO.ProductListItem();
        item.setId(product.getId());
        item.setName(product.getName());
        item.setPrice(product.getPrice());
        item.setOriginalPrice(product.getOriginalPrice());
        item.setImageUrl(product.getImageUrl());
        item.setLocation(product.getLocation());
        item.setViewCount(product.getViewCount());
        item.setCreatedAt(product.getCreatedAt().format(DATE_FORMATTER));
        item.setStatus(product.getStatus());
        item.setRejectReason(product.getRejectReason());
        
        ProductDTO.SellerInfo sellerInfo = new ProductDTO.SellerInfo();
        sellerInfo.setId(product.getSeller().getId());
        sellerInfo.setUsername(product.getSeller().getUsername());
        sellerInfo.setNickname(product.getSeller().getNickname());
        sellerInfo.setAvatar(product.getSeller().getAvatar());
        sellerInfo.setCreditScore(product.getSeller().getCreditScore());
        sellerInfo.setCreditLevel(calcCreditLevel(product.getSeller().getCreditScore()));
        item.setSeller(sellerInfo);
        
        return item;
    }
    
    private ProductDTO.ProductDetail convertToDetail(Product product) {
        ProductDTO.ProductDetail detail = new ProductDTO.ProductDetail();
        detail.setId(product.getId());
        detail.setName(product.getName());
        detail.setDescription(product.getDescription());
        detail.setPrice(product.getPrice());
        detail.setOriginalPrice(product.getOriginalPrice());
        detail.setImageUrl(product.getImageUrl());
        detail.setConditionLevel(product.getConditionLevel());
        detail.setLocation(product.getLocation());
        detail.setViewCount(product.getViewCount());
        detail.setStatus(product.getStatus());
        detail.setRejectReason(product.getRejectReason());
        detail.setCreatedAt(product.getCreatedAt().format(DATE_FORMATTER));
        
        if (product.getCategory() != null) {
            detail.setCategoryId(product.getCategory().getId());
            detail.setCategoryName(product.getCategory().getName());
        }
        
        List<ProductImage> images = productImageRepository.findByProductIdOrderBySortOrderAsc(product.getId());
        detail.setImages(images.stream().map(ProductImage::getImageUrl).collect(Collectors.toList()));
        
        ProductDTO.SellerInfo sellerInfo = new ProductDTO.SellerInfo();
        sellerInfo.setId(product.getSeller().getId());
        sellerInfo.setUsername(product.getSeller().getUsername());
        sellerInfo.setNickname(product.getSeller().getNickname());
        sellerInfo.setAvatar(product.getSeller().getAvatar());
        sellerInfo.setCreditScore(product.getSeller().getCreditScore());
        sellerInfo.setCreditLevel(calcCreditLevel(product.getSeller().getCreditScore()));
        detail.setSeller(sellerInfo);
        
        detail.setAvgRating(reviewRepository.getAverageRatingByProductId(product.getId()));
        detail.setReviewCount(reviewRepository.countByProductId(product.getId()));
        
        return detail;
    }
    
    private String calcCreditLevel(Integer score) {
        if (score == null) return "信誉一般";
        if (score >= 90) return "信誉极佳";
        if (score >= 80) return "信誉优秀";
        if (score >= 70) return "信誉良好";
        if (score >= 60) return "信誉一般";
        return "信誉不佳";
    }
}
