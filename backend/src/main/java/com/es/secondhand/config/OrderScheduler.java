package com.es.secondhand.config;

import com.es.secondhand.entity.Order;
import com.es.secondhand.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务：每分钟扫描一次，自动取消超过 30 分钟未支付的待支付订单
 */
@Component
public class OrderScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderScheduler.class);

    /** 待支付超时时间（分钟） */
    private static final long EXPIRE_MINUTES = 30;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 每 60 秒执行一次，取消所有超时未支付的订单
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime expireTime = LocalDateTime.now().minusMinutes(EXPIRE_MINUTES);
        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(expireTime);

        if (expiredOrders.isEmpty()) {
            return;
        }

        log.info("[自动取消] 发现 {} 笔超时未支付订单，开始处理...", expiredOrders.size());

        for (Order order : expiredOrders) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.info("[自动取消] 订单 {} 已自动取消（创建于 {}）", order.getOrderNo(), order.getCreatedAt());
        }
    }
}
