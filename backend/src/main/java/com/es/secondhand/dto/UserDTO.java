package com.es.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserDTO {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20之间")
        private String password;

        @NotBlank(message = "身份证号不能为空")
        @Pattern(
            regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dX]$",
            message = "身份证号格式不正确"
        )
        private String idCard;

        private String email;

        private String phoneNumber;
    }
    
    @Data
    public static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
    }
    
    @Data
    public static class LoginResponse {
        private Integer id;
        private String username;
        private String nickname;
        private String avatar;
        private String token;
        /** 用户角色：USER / ADMIN，前端用于判断是否显示管理员入口 */
        private String role;
    }
    
    @Data
    public static class UserInfo {
        private Integer id;
        private String username;
        private String nickname;
        private String avatar;
        private String email;
        private String phoneNumber;
        /** 脱敏后的身份证号，如：310***********1234 */
        private String idCard;
        private Integer creditScore;
        private String creditLevel;
        private String createdAt;
        /** 用户角色：USER / ADMIN */
        private String role;
    }
    
    @Data
    public static class UpdateRequest {
        private String nickname;
        private String avatar;
        private String email;
        private String phoneNumber;
    }

    /** 管理员审核注册用户时看到的用户信息（含原始身份证号） */
    @Data
    public static class UserAuditInfo {
        private Integer id;
        private String username;
        private String nickname;
        private String phoneNumber;
        private String email;
        /** 管理员可见完整身份证号 */
        private String idCard;
        private String createdAt;
    }

    /** 管理员审核注册请求 */
    @Data
    public static class UserAuditRequest {
        @NotBlank(message = "审核结果不能为空")
        /** APPROVE 或 REJECT */
        private String action;
        /** 拒绝时填写原因（可选） */
        private String reason;
    }
}
