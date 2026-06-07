package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import com.es.secondhand.dto.ProductDTO;
import com.es.secondhand.dto.ReviewDTO;
import com.es.secondhand.dto.UserDTO;
import com.es.secondhand.entity.Product;
import com.es.secondhand.entity.ProductImage;
import com.es.secondhand.repository.ProductImageRepository;
import com.es.secondhand.repository.ProductRepository;
import com.es.secondhand.service.ReviewService;
import com.es.secondhand.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员审核", description = "注册审核 + 商品审核 + 评价信用分审核")
public class AdminController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserService userService;

    // ==================== 注册审核 ====================

    @GetMapping("/users/pending")
    @Operation(summary = "获取待审核注册用户列表")
    public ApiResponse<Page<UserDTO.UserAuditInfo>> getPendingUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        return ApiResponse.success(userService.getPendingUsers(pageRequest));
    }

    @PostMapping("/users/{id}/audit")
    @Operation(summary = "审核注册用户（通过/拒绝）")
    public ApiResponse<Void> auditUser(
            @PathVariable Integer id,
            @RequestBody UserDTO.UserAuditRequest request) {
        try {
            userService.auditUser(id, request);
            String msg = "APPROVE".equalsIgnoreCase(request.getAction()) ? "已通过，用户可正常登录" : "已拒绝";
            return ApiResponse.success(msg, null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ==================== 商品审核 ====================

    @GetMapping("/products/pending")
    @Operation(summary = "获取待审核商品列表")
    public ApiResponse<Page<ProductDTO.ProductListItem>> getPendingProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Product> pendingProducts = productRepository.findByStatus(3, pageRequest);
        Page<ProductDTO.ProductListItem> result = pendingProducts.map(p -> {
            ProductDTO.ProductListItem item = new ProductDTO.ProductListItem();
            item.setId(p.getId());
            item.setName(p.getName());
            item.setPrice(p.getPrice());
            item.setOriginalPrice(p.getOriginalPrice());
            item.setImageUrl(p.getImageUrl());
            List<String> images = productImageRepository.findByProductIdOrderBySortOrderAsc(p.getId())
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList());
            item.setImages(images);
            item.setLocation(p.getLocation());
            item.setViewCount(p.getViewCount());
            item.setStatus(p.getStatus());
            item.setRejectReason(p.getRejectReason());
            if (p.getCreatedAt() != null) {
                item.setCreatedAt(p.getCreatedAt().toString());
            }
            if (p.getSeller() != null) {
                ProductDTO.SellerInfo sellerInfo = new ProductDTO.SellerInfo();
                sellerInfo.setId(p.getSeller().getId());
                sellerInfo.setUsername(p.getSeller().getUsername());
                sellerInfo.setNickname(p.getSeller().getNickname());
                item.setSeller(sellerInfo);
            }
            return item;
        });
        return ApiResponse.success(result);
    }

    @PostMapping("/products/{id}/review")
    @Operation(summary = "审核商品")
    public ApiResponse<Void> reviewProduct(
            @PathVariable Integer id,
            @RequestBody ProductDTO.AdminReviewRequest request) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("商品不存在"));

            if (request.getApproved()) {
                product.setStatus(1);
                product.setRejectReason(null);
            } else {
                product.setStatus(4);
                product.setRejectReason(request.getRejectReason());
            }

            productRepository.save(product);
            return ApiResponse.success(request.getApproved() ? "审核通过" : "已拒绝", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    // ==================== 评价信用分审核 ====================

    /**
     * 获取待审核的差评列表（评分 1-2 星，尚未生效扣分）
     */
    @GetMapping("/reviews/pending")
    @Operation(summary = "获取待审核差评列表")
    public ApiResponse<Page<ReviewDTO.ReviewInfo>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ReviewDTO.ReviewInfo> reviews = reviewService.getPendingReviews(pageRequest);
        return ApiResponse.success(reviews);
    }

    /**
     * 审核评价：通过则执行扣信用分，拒绝则不扣分
     */
    @PostMapping("/reviews/{id}/audit")
    @Operation(summary = "审核差评（决定是否扣卖家信用分）")
    public ApiResponse<ReviewDTO.ReviewInfo> auditReview(
            @PathVariable Integer id,
            @RequestBody ReviewDTO.AdminAuditRequest request) {
        try {
            ReviewDTO.ReviewInfo result = reviewService.auditReview(id, request);
            String msg = request.getApproved() ? "审核通过，信用分已扣除" : "已拒绝，信用分不变";
            return ApiResponse.success(msg, result);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
