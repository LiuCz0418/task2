package com.es.secondhand.repository;

import com.es.secondhand.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /** 按审核状态查询（0=待审核，1=已通过，2=已拒绝） */
    Page<Review> findByAuditStatus(Integer auditStatus, Pageable pageable);

    /** 查询商品的评价（只展示已通过的） */
    Page<Review> findByProductIdAndAuditStatus(Integer productId, Integer auditStatus, Pageable pageable);

    /** 所有评价（不过滤状态，供用户查看自己发的） */
    Page<Review> findByUserId(Integer userId, Pageable pageable);

    /** 查询卖家收到的评价（只展示已通过的） */
    @Query("SELECT r FROM Review r WHERE r.product.seller.id = :sellerId AND r.auditStatus = :auditStatus")
    Page<Review> findBySellerIdAndAuditStatus(@Param("sellerId") Integer sellerId,
                                               @Param("auditStatus") Integer auditStatus,
                                               Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.auditStatus = 1")
    Double getAverageRatingByProductId(@Param("productId") Integer productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.seller.id = :sellerId AND r.auditStatus = 1")
    Double getAverageRatingBySellerId(@Param("sellerId") Integer sellerId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.auditStatus = 1")
    Long countByProductId(@Param("productId") Integer productId);

    boolean existsByOrderId(Integer orderId);

    /** 根据订单ID查找评价（每个订单最多一条） */
    java.util.Optional<Review> findByOrderId(Integer orderId);
}

