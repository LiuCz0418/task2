package com.es.secondhand.service.impl;

import com.es.secondhand.dto.OrderDTO;
import com.es.secondhand.entity.Order;
import com.es.secondhand.entity.Product;
import com.es.secondhand.entity.User;
import com.es.secondhand.repository.OrderRepository;
import com.es.secondhand.repository.ProductRepository;
import com.es.secondhand.repository.UserRepository;
import com.es.secondhand.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public OrderDTO.OrderInfo createOrder(Integer buyerId, OrderDTO.CreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架或已售出");
        }
        
        if (product.getSeller().getId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }
        
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProduct(product);
        order.setBuyer(buyer);
        order.setSeller(product.getSeller());
        order.setPrice(request.getPrice());
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingName(request.getShippingName());
        order.setShippingPhone(request.getShippingPhone());
        order.setStatus(Order.OrderStatus.PENDING);
        
        order = orderRepository.save(order);
        
        return convertToOrderInfo(order);
    }
    
    @Override
    @Transactional
    public OrderDTO.OrderInfo payOrder(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getBuyer().getId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(Order.OrderStatus.PAID);
        order.setPaymentTime(LocalDateTime.now());
        
        Product product = order.getProduct();
        product.setStatus(2);
        productRepository.save(product);
        
        order = orderRepository.save(order);
        return convertToOrderInfo(order);
    }
    
    @Override
    @Transactional
    public OrderDTO.OrderInfo shipOrder(Integer orderId, Integer sellerId, OrderDTO.ShipRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(Order.OrderStatus.SHIPPED);
        // 自动生成快递单号：ES + yyyyMMddHHmmss + 订单ID补零5位
        String trackingNo = "ES" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%05d", orderId);
        order.setTrackingNumber(trackingNo);
        order.setTrackingCompany(request.getTrackingCompany());
        order.setShipTime(LocalDateTime.now());
        
        order = orderRepository.save(order);
        return convertToOrderInfo(order);
    }
    
    @Override
    @Transactional
    public OrderDTO.OrderInfo confirmReceive(Integer orderId, Integer buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (order.getStatus() != Order.OrderStatus.SHIPPED) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(Order.OrderStatus.COMPLETED);
        order.setReceiveTime(LocalDateTime.now());
        
        order = orderRepository.save(order);
        return convertToOrderInfo(order);
    }
    
    @Override
    @Transactional
    public OrderDTO.OrderInfo cancelOrder(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getBuyer().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("只能取消待支付的订单");
        }
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        return convertToOrderInfo(order);
    }
    
    @Override
    public OrderDTO.OrderInfo getOrderDetail(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!order.getBuyer().getId().equals(userId) && !order.getSeller().getId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }
        
        return convertToOrderInfo(order);
    }
    
    @Override
    public Page<OrderDTO.OrderInfo> getBuyerOrders(Integer buyerId, String status, Pageable pageable) {
        Page<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findByBuyerIdAndStatus(buyerId, Order.OrderStatus.valueOf(status), pageable);
        } else {
            orders = orderRepository.findByBuyerId(buyerId, pageable);
        }
        return orders.map(this::convertToOrderInfo);
    }
    
    @Override
    public Page<OrderDTO.OrderInfo> getSellerOrders(Integer sellerId, String status, Pageable pageable) {
        Page<Order> orders;
        if (status != null && !status.isEmpty()) {
            orders = orderRepository.findBySellerIdAndStatus(sellerId, Order.OrderStatus.valueOf(status), pageable);
        } else {
            orders = orderRepository.findBySellerId(sellerId, pageable);
        }
        return orders.map(this::convertToOrderInfo);
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
    
    private OrderDTO.OrderInfo convertToOrderInfo(Order order) {
        OrderDTO.OrderInfo info = new OrderDTO.OrderInfo();
        info.setId(order.getId());
        info.setOrderNo(order.getOrderNo());
        info.setProductId(order.getProduct().getId());
        info.setProductName(order.getProduct().getName());
        info.setProductImage(order.getProduct().getImageUrl());
        info.setPrice(order.getPrice());
        info.setStatus(order.getStatus().name());
        info.setShippingAddress(order.getShippingAddress());
        info.setShippingName(order.getShippingName());
        info.setShippingPhone(order.getShippingPhone());
        info.setTrackingNumber(order.getTrackingNumber());
        info.setTrackingCompany(order.getTrackingCompany());
        info.setCreatedAt(order.getCreatedAt().format(DATE_FORMATTER));
        
        if (order.getPaymentTime() != null) {
            info.setPaymentTime(order.getPaymentTime().format(DATE_FORMATTER));
        }
        if (order.getShipTime() != null) {
            info.setShipTime(order.getShipTime().format(DATE_FORMATTER));
        }
        if (order.getReceiveTime() != null) {
            info.setReceiveTime(order.getReceiveTime().format(DATE_FORMATTER));
        }
        
        OrderDTO.BuyerSellerInfo buyerInfo = new OrderDTO.BuyerSellerInfo();
        buyerInfo.setId(order.getBuyer().getId());
        buyerInfo.setUsername(order.getBuyer().getUsername());
        buyerInfo.setNickname(order.getBuyer().getNickname());
        buyerInfo.setAvatar(order.getBuyer().getAvatar());
        info.setBuyer(buyerInfo);
        
        OrderDTO.BuyerSellerInfo sellerInfo = new OrderDTO.BuyerSellerInfo();
        sellerInfo.setId(order.getSeller().getId());
        sellerInfo.setUsername(order.getSeller().getUsername());
        sellerInfo.setNickname(order.getSeller().getNickname());
        sellerInfo.setAvatar(order.getSeller().getAvatar());
        info.setSeller(sellerInfo);
        
        return info;
    }
}
