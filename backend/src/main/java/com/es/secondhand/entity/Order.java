package com.es.secondhand.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    
    public enum OrderStatus {
        PENDING, PAID, SHIPPED, DELIVERED, COMPLETED, CANCELLED
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "order_no", nullable = false, unique = true, length = 32)
    private String orderNo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;
    
    @Column(name = "shipping_name", length = 50)
    private String shippingName;
    
    @Column(name = "shipping_phone", length = 20)
    private String shippingPhone;
    
    @Column(name = "tracking_number", length = 50)
    private String trackingNumber;
    
    @Column(name = "tracking_company", length = 50)
    private String trackingCompany;
    
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;
    
    @Column(name = "ship_time")
    private LocalDateTime shipTime;
    
    @Column(name = "receive_time")
    private LocalDateTime receiveTime;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
