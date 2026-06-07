package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import com.es.secondhand.dto.OrderDTO;
import com.es.secondhand.entity.User;
import com.es.secondhand.service.OrderService;
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
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "订单的创建、支付、发货、收货")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "创建订单")
    public ApiResponse<OrderDTO.OrderInfo> createOrder(@Valid @RequestBody OrderDTO.CreateRequest request) {
        try {
            User user = userService.getCurrentUser();
            OrderDTO.OrderInfo orderInfo = orderService.createOrder(user.getId(), request);
            return ApiResponse.success("订单创建成功", orderInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/{id}/pay")
    @Operation(summary = "支付订单")
    public ApiResponse<OrderDTO.OrderInfo> payOrder(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            OrderDTO.OrderInfo orderInfo = orderService.payOrder(id, user.getId());
            return ApiResponse.success("支付成功", orderInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/{id}/ship")
    @Operation(summary = "发货")
    public ApiResponse<OrderDTO.OrderInfo> shipOrder(
            @PathVariable Integer id,
            @RequestBody OrderDTO.ShipRequest request) {
        try {
            User user = userService.getCurrentUser();
            OrderDTO.OrderInfo orderInfo = orderService.shipOrder(id, user.getId(), request);
            return ApiResponse.success("发货成功", orderInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/{id}/receive")
    @Operation(summary = "确认收货")
    public ApiResponse<OrderDTO.OrderInfo> confirmReceive(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            OrderDTO.OrderInfo orderInfo = orderService.confirmReceive(id, user.getId());
            return ApiResponse.success("确认收货成功", orderInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public ApiResponse<OrderDTO.OrderInfo> cancelOrder(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            OrderDTO.OrderInfo orderInfo = orderService.cancelOrder(id, user.getId());
            return ApiResponse.success("订单已取消", orderInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情")
    public ApiResponse<OrderDTO.OrderInfo> getOrderDetail(@PathVariable Integer id) {
        try {
            User user = userService.getCurrentUser();
            OrderDTO.OrderInfo orderInfo = orderService.getOrderDetail(id, user.getId());
            return ApiResponse.success(orderInfo);
        } catch (Exception e) {
            return ApiResponse.error(404, e.getMessage());
        }
    }
    
    @GetMapping
    @Operation(summary = "获取我的订单列表（作为买家）")
    public ApiResponse<Page<OrderDTO.OrderInfo>> getBuyerOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userService.getCurrentUser();
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<OrderDTO.OrderInfo> orders = orderService.getBuyerOrders(user.getId(), status, pageRequest);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/sold")
    @Operation(summary = "获取我卖出的订单列表（作为卖家）")
    public ApiResponse<Page<OrderDTO.OrderInfo>> getSellerOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            User user = userService.getCurrentUser();
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<OrderDTO.OrderInfo> orders = orderService.getSellerOrders(user.getId(), status, pageRequest);
            return ApiResponse.success(orders);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
}
