package com.es.secondhand.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(columnDefinition = "TEXT")
    private String comment;
    
    @Column(columnDefinition = "TEXT")
    private String images;
    
    // 多维度评分
    @Column(name = "service_score")
    private Integer serviceScore;
    
    @Column(name = "desc_score")
    private Integer descScore;
    
    @Column(name = "ship_score")
    private Integer shipScore;
    
    /**
     * 审核状态：0-待审核（扣信用分评价需管理员审核），1-已通过/直接生效，2-已拒绝
     * 评分 1-2 星会触发扣信用分，需管理员审核后才生效；
     * 评分 3-5 星直接生效（auditStatus=1）。
     */
    @Column(name = "audit_status")
    private Integer auditStatus = 1;

    /**
     * 管理员审核备注（拒绝原因等）
     */
    @Column(name = "audit_remark", length = 255)
    private String auditRemark;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
