package com.es.secondhand.service;

import com.es.secondhand.dto.OrderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    
    OrderDTO.OrderInfo createOrder(Integer buyerId, OrderDTO.CreateRequest request);
    
    OrderDTO.OrderInfo payOrder(Integer orderId, Integer userId);
    
    OrderDTO.OrderInfo shipOrder(Integer orderId, Integer sellerId, OrderDTO.ShipRequest request);
    
    OrderDTO.OrderInfo confirmReceive(Integer orderId, Integer buyerId);
    
    OrderDTO.OrderInfo cancelOrder(Integer orderId, Integer userId);
    
    OrderDTO.OrderInfo getOrderDetail(Integer orderId, Integer userId);
    
    Page<OrderDTO.OrderInfo> getBuyerOrders(Integer buyerId, String status, Pageable pageable);
    
    Page<OrderDTO.OrderInfo> getSellerOrders(Integer sellerId, String status, Pageable pageable);
}
