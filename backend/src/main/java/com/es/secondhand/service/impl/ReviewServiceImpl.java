package com.es.secondhand.service.impl;

import com.es.secondhand.dto.ReviewDTO;
import com.es.secondhand.entity.Order;
import com.es.secondhand.entity.Product;
import com.es.secondhand.entity.Review;
import com.es.secondhand.entity.User;
import com.es.secondhand.repository.OrderRepository;
import com.es.secondhand.repository.ProductRepository;
import com.es.secondhand.repository.ReviewRepository;
import com.es.secondhand.repository.UserRepository;
import com.es.secondhand.service.MessageService;
import com.es.secondhand.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MessageService messageService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 计算信用分变动值（评分 → delta）
     * 5星 +2，4星 +1，3星 0，2星 -3，1星 -5
     */
    private int calcCreditDelta(int rating) {
        switch (rating) {
            case 5: return 2;
            case 4: return 1;
            case 3: return 0;
            case 2: return -3;
            case 1: return -5;
            default: return 0;
        }
    }

    /**
     * 将信用分变动应用到卖家账户（限制在 0~100 范围内）
     */
    private void applyCreditDelta(User seller, int delta) {
        if (delta == 0) return;
        int newScore = Math.max(0, Math.min(100, seller.getCreditScore() + delta));
        seller.setCreditScore(newScore);
        userRepository.save(seller);
    }
    
    @Override
    @Transactional
    public ReviewDTO.ReviewInfo createReview(Integer userId, ReviewDTO.CreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setImages(request.getImages());
        review.setServiceScore(request.getServiceScore());
        review.setDescScore(request.getDescScore());
        review.setShipScore(request.getShipScore());
        
        if (request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            if (!order.getBuyer().getId().equals(userId)) {
                throw new RuntimeException("只能评价自己购买的商品");
            }
            if (order.getStatus() != Order.OrderStatus.COMPLETED) {
                throw new RuntimeException("只能评价已完成的订单");
            }
            if (reviewRepository.existsByOrderId(request.getOrderId())) {
                throw new RuntimeException("该订单已评价");
            }
            review.setOrder(order);
        }

        int delta = calcCreditDelta(request.getRating());
        if (delta < 0) {
            // 需要扣信用分 → 进入待审核，不立即扣分
            review.setAuditStatus(0);
        } else {
            // 不扣分（加分或无变化）→ 直接生效
            review.setAuditStatus(1);
            applyCreditDelta(product.getSeller(), delta);
        }
        
        review = reviewRepository.save(review);
        return convertToReviewInfo(review);
    }

    @Override
    @Transactional
    public ReviewDTO.ReviewInfo auditReview(Integer reviewId, ReviewDTO.AdminAuditRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        if (review.getAuditStatus() != 0) {
            throw new RuntimeException("该评价已审核，无需重复操作");
        }

        if (request.getApproved()) {
            // 审核通过：生效扣分
            review.setAuditStatus(1);
            review.setAuditRemark(null);
            int delta = calcCreditDelta(review.getRating());
            applyCreditDelta(review.getProduct().getSeller(), delta);
        } else {
            // 审核拒绝：不扣分
            review.setAuditStatus(2);
            review.setAuditRemark(request.getRemark());

            // 向买家发送系统通知
            String productName = review.getProduct().getName();
            String reason = (request.getRemark() != null && !request.getRemark().isBlank())
                    ? "原因：" + request.getRemark()
                    : "请补充更具体的评价内容";
            String notifyContent = String.format(
                    "【评价审核未通过】您对商品「%s」提交的差评未通过管理员审核，%s。",
                    productName, reason);
            messageService.sendSystemNotification(review.getUser().getId(), notifyContent);
        }

        review = reviewRepository.save(review);
        return convertToReviewInfo(review);
    }

    @Override
    public Page<ReviewDTO.ReviewInfo> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByAuditStatus(0, pageable)
                .map(this::convertToReviewInfo);
    }
    
    @Override
    public Page<ReviewDTO.ReviewInfo> getProductReviews(Integer productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndAuditStatus(productId, 1, pageable)
                .map(this::convertToReviewInfo);
    }
    
    @Override
    public Page<ReviewDTO.ReviewInfo> getUserReviews(Integer userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(this::convertToReviewInfo);
    }
    
    @Override
    public Page<ReviewDTO.ReviewInfo> getSellerReviews(Integer sellerId, Pageable pageable) {
        return reviewRepository.findBySellerIdAndAuditStatus(sellerId, 1, pageable)
                .map(this::convertToReviewInfo);
    }
    
    @Override
    public boolean checkOrderReviewed(Integer orderId) {
        return reviewRepository.existsByOrderId(orderId);
    }

    @Override
    @Transactional
    public ReviewDTO.ReviewInfo updateReview(Integer reviewId, Integer userId, ReviewDTO.UpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        // 只有评价所有者才能修改
        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权修改此评价");
        }

        // 只有被拒绝的评价（auditStatus=2）才可以修改
        if (review.getAuditStatus() != 2) {
            throw new RuntimeException("只有审核被拒绝的评价才可以修改");
        }

        // 更新评价内容
        review.setRating(request.getRating());
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
        if (request.getImages() != null) {
            review.setImages(request.getImages());
        }
        if (request.getServiceScore() != null) {
            review.setServiceScore(request.getServiceScore());
        }
        if (request.getDescScore() != null) {
            review.setDescScore(request.getDescScore());
        }
        if (request.getShipScore() != null) {
            review.setShipScore(request.getShipScore());
        }

        // 清除拒绝原因，重置审核状态
        review.setAuditRemark(null);

        int delta = calcCreditDelta(request.getRating());
        if (delta < 0) {
            // 仍是差评，重新进入待审核
            review.setAuditStatus(0);
        } else {
            // 改成了好评/中评，直接生效并加分
            review.setAuditStatus(1);
            applyCreditDelta(review.getProduct().getSeller(), delta);
        }

        review = reviewRepository.save(review);
        return convertToReviewInfo(review);
    }

    @Override
    public ReviewDTO.ReviewInfo getReviewByOrderId(Integer orderId) {
        return reviewRepository.findByOrderId(orderId)
                .map(this::convertToReviewInfo)
                .orElse(null);
    }
    
    private ReviewDTO.ReviewInfo convertToReviewInfo(Review review) {
        ReviewDTO.ReviewInfo info = new ReviewDTO.ReviewInfo();
        info.setId(review.getId());
        info.setProductId(review.getProduct().getId());
        info.setProductName(review.getProduct().getName());
        info.setRating(review.getRating());
        info.setComment(review.getComment());
        info.setImages(review.getImages());
        info.setServiceScore(review.getServiceScore());
        info.setDescScore(review.getDescScore());
        info.setShipScore(review.getShipScore());
        info.setAuditStatus(review.getAuditStatus());
        info.setAuditRemark(review.getAuditRemark());
        info.setCreditDelta(calcCreditDelta(review.getRating()));
        if (review.getCreatedAt() != null) {
            info.setCreatedAt(review.getCreatedAt().format(DATE_FORMATTER));
        }
        
        ReviewDTO.UserInfo userInfo = new ReviewDTO.UserInfo();
        userInfo.setId(review.getUser().getId());
        userInfo.setUsername(review.getUser().getUsername());
        userInfo.setNickname(review.getUser().getNickname());
        userInfo.setAvatar(review.getUser().getAvatar());
        info.setUser(userInfo);
        
        return info;
    }
}
