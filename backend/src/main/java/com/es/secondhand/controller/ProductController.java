package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import com.es.secondhand.dto.ProductDTO;
import com.es.secondhand.entity.Category;
import com.es.secondhand.entity.User;
import com.es.secondhand.service.ProductService;
import com.es.secondhand.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "商品管理", description = "商品的发布、查询、修改、删除")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/categories")
    @Operation(summary = "获取所有分类")
    public ApiResponse<List<Category>> getCategories() {
        List<Category> categories = productService.getAllCategories();
        return ApiResponse.success(categories);
    }
    
    @GetMapping("/products")
    @Operation(summary = "获取商品列表")
    public ApiResponse<Page<ProductDTO.ProductListItem>> getProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductDTO.ProductListItem> products = productService.getProducts(categoryId, keyword, pageRequest);
        return ApiResponse.success(products);
    }
    
    @GetMapping("/products/{id}")
    @Operation(summary = "获取商品详情")
    public ApiResponse<ProductDTO.ProductDetail> getProductDetail(@PathVariable Integer id) {
        try {
            ProductDTO.ProductDetail detail = productService.getProductDetail(id);
            return ApiResponse.success(detail);
        } catch (Exception e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
    
    @PostMapping("/products")
    @Operation(summary = "发布商品")
    public ApiResponse<ProductDTO.ProductDetail> createProduct(@Valid @RequestBody ProductDTO.CreateRequest request) {
        try {
            User user = userService.getCurrentUser();
            ProductDTO.ProductDetail detail = productService.createProduct(user.getId(), request);
            return ApiResponse.success("发布成功", detail);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PutMapping("/products/{id}")
    @Operation(summary = "更新商品")
    public ApiResponse<ProductDTO.ProductDetail> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductDTO.UpdateRequest request) {
        try {
            User user = userService.getCurrentUser();
            ProductDTO.ProductDetail detail = productService.updateProduct(id, user.getId(), request);
            return ApiResponse.success("更新成功", detail);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @DeleteMapping("/products/{id}")
    @Operation(summary = "删除商品")
    public ApiResponse<Void> deleteProduct(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            productService.deleteProduct(id, user.getId());
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/products/batch-delete")
    @Operation(summary = "批量删除商品")
    public ApiResponse<Void> batchDeleteProducts(@RequestBody List<Integer> productIds) {
        try {
            User user = userService.getCurrentUser();
            productService.batchDeleteProducts(productIds, user.getId());
            return ApiResponse.success("批量删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/products/my")
    @Operation(summary = "获取我发布的商品")
    public ApiResponse<Page<ProductDTO.ProductListItem>> getMyProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        try {
            User user = userService.getCurrentUser();
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ProductDTO.ProductListItem> products = productService.getMyProducts(user.getId(), status, pageRequest);
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/products/hot")
    @Operation(summary = "获取热门商品")
    public ApiResponse<List<ProductDTO.ProductListItem>> getHotProducts() {
        List<ProductDTO.ProductListItem> products = productService.getHotProducts();
        return ApiResponse.success(products);
    }
    
    @GetMapping("/products/latest")
    @Operation(summary = "获取最新商品")
    public ApiResponse<List<ProductDTO.ProductListItem>> getLatestProducts() {
        List<ProductDTO.ProductListItem> products = productService.getLatestProducts();
        return ApiResponse.success(products);
    }
    
    // ==================== 收藏相关接口 ====================
    
    @PostMapping("/products/{id}/favorite")
    @Operation(summary = "添加收藏")
    public ApiResponse<Void> addFavorite(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            productService.addFavorite(user.getId(), id);
            return ApiResponse.success("收藏成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @DeleteMapping("/products/{id}/favorite")
    @Operation(summary = "取消收藏")
    public ApiResponse<Void> removeFavorite(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            productService.removeFavorite(user.getId(), id);
            return ApiResponse.success("取消收藏成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/products/{id}/favorite")
    @Operation(summary = "检查是否已收藏")
    public ApiResponse<Boolean> isFavorite(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            boolean isFav = productService.isFavorite(user.getId(), id);
            return ApiResponse.success(isFav);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/favorites")
    @Operation(summary = "获取我的收藏列表")
    public ApiResponse<Page<ProductDTO.ProductListItem>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userService.getCurrentUser();
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ProductDTO.ProductListItem> favorites = productService.getFavorites(user.getId(), pageRequest);
            return ApiResponse.success(favorites);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
}
