package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import com.es.secondhand.dto.ReviewDTO;
import com.es.secondhand.entity.User;
import com.es.secondhand.service.ReviewService;
import com.es.secondhand.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "评价管理", description = "商品评价的创建和查询")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "提交评价")
    public ApiResponse<ReviewDTO.ReviewInfo> createReview(@Valid @RequestBody ReviewDTO.CreateRequest request) {
        try {
            User user = userService.getCurrentUser();
            ReviewDTO.ReviewInfo reviewInfo = reviewService.createReview(user.getId(), request);
            return ApiResponse.success("评价成功", reviewInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/{productId}")
    @Operation(summary = "获取商品评价列表")
    public ApiResponse<Page<ReviewDTO.ReviewInfo>> getProductReviews(
            @PathVariable Integer productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewDTO.ReviewInfo> reviews = reviewService.getProductReviews(productId, pageRequest);
        return ApiResponse.success(reviews);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户评价列表")
    public ApiResponse<Page<ReviewDTO.ReviewInfo>> getUserReviews(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewDTO.ReviewInfo> reviews = reviewService.getUserReviews(userId, pageRequest);
        return ApiResponse.success(reviews);
    }
    
    @GetMapping("/seller/{userId}")
    @Operation(summary = "获取卖家收到的评价列表")
    public ApiResponse<Page<ReviewDTO.ReviewInfo>> getSellerReviews(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewDTO.ReviewInfo> reviews = reviewService.getSellerReviews(userId, pageRequest);
        return ApiResponse.success(reviews);
    }
    
    @GetMapping("/order/{orderId}/exists")
    @Operation(summary = "检查订单是否已评价")
    public ApiResponse<Boolean> checkOrderReviewed(@PathVariable Integer orderId) {
        boolean reviewed = reviewService.checkOrderReviewed(orderId);
        return ApiResponse.success(reviewed);
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "获取订单的评价详情（含审核状态，用于判断是否可修改）")
    public ApiResponse<ReviewDTO.ReviewInfo> getReviewByOrderId(@PathVariable Integer orderId) {
        ReviewDTO.ReviewInfo info = reviewService.getReviewByOrderId(orderId);
        return ApiResponse.success(info);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改被拒绝的评价并重新提交审核")
    public ApiResponse<ReviewDTO.ReviewInfo> updateReview(
            @PathVariable Integer id,
            @Valid @RequestBody ReviewDTO.UpdateRequest request) {
        try {
            User user = userService.getCurrentUser();
            ReviewDTO.ReviewInfo reviewInfo = reviewService.updateReview(id, user.getId(), request);
            int auditStatus = reviewInfo.getAuditStatus();
            String msg = auditStatus == 0 ? "已重新提交审核，管理员审核通过后生效" : "评价已更新";
            return ApiResponse.success(msg, reviewInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
