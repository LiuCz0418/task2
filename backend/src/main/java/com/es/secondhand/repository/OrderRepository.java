package com.es.secondhand.repository;

import com.es.secondhand.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    Optional<Order> findByOrderNo(String orderNo);
    
    Page<Order> findByBuyerId(Integer buyerId, Pageable pageable);
    
    Page<Order> findBySellerId(Integer sellerId, Pageable pageable);
    
    Page<Order> findByBuyerIdAndStatus(Integer buyerId, Order.OrderStatus status, Pageable pageable);
    
    Page<Order> findBySellerIdAndStatus(Integer sellerId, Order.OrderStatus status, Pageable pageable);
    
    boolean existsByProductIdAndBuyerIdAndStatusNot(Integer productId, Integer buyerId, Order.OrderStatus status);

    /**
     * 查询所有创建时间早于 expireTime、且状态仍为 PENDING 的订单（即超时未支付）
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :expireTime")
    List<Order> findExpiredPendingOrders(@Param("expireTime") LocalDateTime expireTime);
}
