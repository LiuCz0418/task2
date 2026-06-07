package com.es.secondhand.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class MessageDTO {
    
    @Data
    public static class SendRequest {
        @NotNull(message = "接收者ID不能为空")
        private Integer receiverId;
        
        private Integer productId;
        
        @NotBlank(message = "消息内容不能为空")
        private String content;
        
        private Integer messageType = 1;
    }
    
    @Data
    public static class MessageInfo {
        private Integer id;
        private Integer senderId;
        private String senderName;
        private String senderAvatar;
        private Integer receiverId;
        private String receiverName;
        private String receiverAvatar;
        private Integer productId;
        private String content;
        private Integer messageType;
        private Integer isRead;
        private String createdAt;
    }
    
    @Data
    public static class ConversationInfo {
        private Integer userId;
        private String username;
        private String nickname;
        private String avatar;
        private String lastMessage;
        private String lastTime;
        private Long unreadCount;
    }
}
