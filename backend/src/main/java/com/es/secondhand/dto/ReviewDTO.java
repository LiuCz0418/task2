package com.es.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ReviewDTO {
    
    @Data
    public static class CreateRequest {
        @NotNull(message = "商品ID不能为空")
        private Integer productId;
        
        private Integer orderId;
        
        @NotNull(message = "评分不能为空")
        @Min(value = 1, message = "评分最小为1")
        @Max(value = 5, message = "评分最大为5")
        private Integer rating;
        
        private String comment;

        /** 图片URL列表（JSON数组字符串，由前端上传图片后传入） */
        private String images;
        
        // 多维度评分（可选，1-5分）
        @Min(value = 1) @Max(value = 5)
        private Integer serviceScore;
        
        @Min(value = 1) @Max(value = 5)
        private Integer descScore;
        
        @Min(value = 1) @Max(value = 5)
        private Integer shipScore;
    }
    
    @Data
    public static class ReviewInfo {
        private Integer id;
        private Integer productId;
        private String productName;
        private Integer rating;
        private String comment;
        /** 图片URL列表（JSON数组字符串） */
        private String images;
        private String createdAt;
        // 多维度评分
        private Integer serviceScore;
        private Integer descScore;
        private Integer shipScore;
        private UserInfo user;
        /** 审核状态：0-待审核，1-已通过，2-已拒绝 */
        private Integer auditStatus;
        /** 管理员审核备注 */
        private String auditRemark;
        /** 该评价对应的信用分变动值（负数表示扣分，正数表示加分，0表示无变动） */
        private Integer creditDelta;
    }
    
    @Data
    public static class UserInfo {
        private Integer id;
        private String username;
        private String nickname;
        private String avatar;
    }

    /** 管理员审核评价请求 */
    @Data
    public static class AdminAuditRequest {
        /** true=通过并生效扣分，false=拒绝不扣分 */
        @NotNull(message = "审核结果不能为空")
        private Boolean approved;
        /** 拒绝时填写原因 */
        private String remark;
    }

    /**
     * 修改被拒绝的评价请求（只允许 auditStatus=2 的评价修改）
     */
    @Data
    public static class UpdateRequest {
        @NotNull(message = "评分不能为空")
        @Min(value = 1, message = "评分最小为1")
        @Max(value = 5, message = "评分最大为5")
        private Integer rating;

        private String comment;

        private String images;

        @Min(value = 1) @Max(value = 5)
        private Integer serviceScore;

        @Min(value = 1) @Max(value = 5)
        private Integer descScore;

        @Min(value = 1) @Max(value = 5)
        private Integer shipScore;
    }
}

