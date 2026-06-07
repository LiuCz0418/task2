package com.es.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

public class OrderDTO {
    
    @Data
    public static class CreateRequest {
        @NotNull(message = "商品ID不能为空")
        private Integer productId;
        
        @NotNull(message = "价格不能为空")
        @Positive(message = "价格必须大于0")
        private BigDecimal price;
        
        private String shippingAddress;
        private String shippingName;
        private String shippingPhone;
    }
    
    @Data
    public static class ShipRequest {
        private String trackingNumber;
        private String trackingCompany;
    }
    
    @Data
    public static class OrderInfo {
        private Integer id;
        private String orderNo;
        private Integer productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private String status;
        private String shippingAddress;
        private String shippingName;
        private String shippingPhone;
        private String trackingNumber;
        private String trackingCompany;
        private BuyerSellerInfo buyer;
        private BuyerSellerInfo seller;
        private String createdAt;
        private String paymentTime;
        private String shipTime;
        private String receiveTime;
    }
    
    @Data
    public static class BuyerSellerInfo {
        private Integer id;
        private String username;
        private String nickname;
        private String avatar;
    }
}
