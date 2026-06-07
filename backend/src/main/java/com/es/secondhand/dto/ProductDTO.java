package com.es.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class ProductDTO {
    
    @Data
    public static class CreateRequest {
        @NotBlank(message = "商品名称不能为空")
        private String name;
        
        private String description;
        
        @NotNull(message = "价格不能为空")
        @Positive(message = "价格必须大于0")
        private BigDecimal price;
        
        private BigDecimal originalPrice;
        
        private Integer categoryId;
        
        private Integer conditionLevel;
        
        private String location;
        
        private List<String> images;
    }
    
    @Data
    public static class UpdateRequest {
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer categoryId;
        private Integer conditionLevel;
        private String location;
        private Integer status;
        private List<String> images;
    }
    
    @Data
    public static class ProductListItem {
        private Integer id;
        private String name;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String imageUrl;
        private List<String> images;
        private String location;
        private Integer viewCount;
        private Integer status;
        private String rejectReason;
        private String createdAt;
        private SellerInfo seller;
    }
    
    @Data
    public static class ProductDetail {
        private Integer id;
        private String name;
        private String description;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private String imageUrl;
        private List<String> images;
        private Integer categoryId;
        private String categoryName;
        private Integer conditionLevel;
        private String location;
        private Integer viewCount;
        private Integer status;
        private String rejectReason;
        private String createdAt;
        private SellerInfo seller;
        private Double avgRating;
        private Long reviewCount;
    }
    
    @Data
    public static class SellerInfo {
        private Integer id;
        private String username;
        private String nickname;
        private String avatar;
        private Integer creditScore;
        private String creditLevel;
    }

    @Data
    public static class AdminReviewRequest {
        private Boolean approved;
        private String rejectReason;
    }
}
