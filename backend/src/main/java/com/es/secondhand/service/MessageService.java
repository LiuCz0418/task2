package com.es.secondhand.service;

import com.es.secondhand.dto.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MessageService {
    
    MessageDTO.MessageInfo sendMessage(Integer senderId, MessageDTO.SendRequest request);

    /**
     * 发送系统通知给指定用户。
     * 由系统中第一个 ADMIN 账号代发，messageType=2（系统通知）。
     * 若系统中暂无管理员账号则静默跳过，不抛出异常。
     */
    void sendSystemNotification(Integer receiverId, String content);
    
    Page<MessageDTO.MessageInfo> getConversation(Integer userId1, Integer userId2, Pageable pageable);
    
    List<MessageDTO.ConversationInfo> getConversationList(Integer userId);
    
    void markAsRead(Integer userId, Integer senderId);
    
    Long getUnreadCount(Integer userId);
}
