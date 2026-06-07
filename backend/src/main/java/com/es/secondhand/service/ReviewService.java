package com.es.secondhand.service;

import com.es.secondhand.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    
    ReviewDTO.ReviewInfo createReview(Integer userId, ReviewDTO.CreateRequest request);
    
    Page<ReviewDTO.ReviewInfo> getProductReviews(Integer productId, Pageable pageable);
    
    Page<ReviewDTO.ReviewInfo> getUserReviews(Integer userId, Pageable pageable);
    
    Page<ReviewDTO.ReviewInfo> getSellerReviews(Integer sellerId, Pageable pageable);
    
    boolean checkOrderReviewed(Integer orderId);

    /**
     * 获取待审核的评价列表（审核状态=0，即低分扣信用分评价）
     */
    Page<ReviewDTO.ReviewInfo> getPendingReviews(Pageable pageable);

    /**
     * 管理员审核评价：通过则扣除卖家信用分并将状态置为1；拒绝则置为2，不扣分
     */
    ReviewDTO.ReviewInfo auditReview(Integer reviewId, ReviewDTO.AdminAuditRequest request);

    /**
     * 买家修改被拒绝的评价，重新提交审核（仅 auditStatus=2 可修改）
     */
    ReviewDTO.ReviewInfo updateReview(Integer reviewId, Integer userId, ReviewDTO.UpdateRequest request);

    /**
     * 根据订单ID获取该订单的评价（不存在时返回null）
     */
    ReviewDTO.ReviewInfo getReviewByOrderId(Integer orderId);
}

