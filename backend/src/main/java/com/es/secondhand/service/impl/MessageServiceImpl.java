package com.es.secondhand.service.impl;

import com.es.secondhand.dto.MessageDTO;
import com.es.secondhand.entity.Message;
import com.es.secondhand.entity.Product;
import com.es.secondhand.entity.User;
import com.es.secondhand.repository.MessageRepository;
import com.es.secondhand.repository.ProductRepository;
import com.es.secondhand.repository.UserRepository;
import com.es.secondhand.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 发送系统通知：由第一个 ADMIN 账号代发给指定买家。
     * messageType=2 表示系统通知，前端可据此展示不同样式。
     */
    @Override
    @Transactional
    public void sendSystemNotification(Integer receiverId, String content) {
        User admin = userRepository.findFirstByRole("ADMIN").orElse(null);
        if (admin == null) return; // 暂无管理员账号，静默跳过

        User receiver = userRepository.findById(receiverId).orElse(null);
        if (receiver == null) return;

        Message message = new Message();
        message.setSender(admin);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setMessageType(2); // 2 = 系统通知
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public MessageDTO.MessageInfo sendMessage(Integer senderId, MessageDTO.SendRequest request) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("接收者不存在"));
        
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            message.setProduct(product);
        }
        
        message = messageRepository.save(message);
        return convertToMessageInfo(message);
    }
    
    @Override
    public Page<MessageDTO.MessageInfo> getConversation(Integer userId1, Integer userId2, Pageable pageable) {
        return messageRepository.findConversation(userId1, userId2, pageable)
                .map(this::convertToMessageInfo);
    }
    
    @Override
    public List<MessageDTO.ConversationInfo> getConversationList(Integer userId) {
        List<Integer> userIds = messageRepository.findConversationUserIds(userId);
        List<MessageDTO.ConversationInfo> conversations = new ArrayList<>();
        
        for (Integer otherUserId : userIds) {
            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser == null) continue;
            
            Page<Message> messages = messageRepository.findConversation(
                    userId, otherUserId,
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt"))
            );
            
            MessageDTO.ConversationInfo info = new MessageDTO.ConversationInfo();
            info.setUserId(otherUser.getId());
            info.setUsername(otherUser.getUsername());
            info.setNickname(otherUser.getNickname());
            info.setAvatar(otherUser.getAvatar());
            
            if (messages.hasContent()) {
                Message lastMsg = messages.getContent().get(0);
                info.setLastMessage(lastMsg.getContent());
                info.setLastTime(lastMsg.getCreatedAt().format(DATE_FORMATTER));
            }
            
            conversations.add(info);
        }
        
        return conversations;
    }
    
    @Override
    @Transactional
    public void markAsRead(Integer userId, Integer senderId) {
        messageRepository.markAsRead(userId, senderId);
    }
    
    @Override
    public Long getUnreadCount(Integer userId) {
        return messageRepository.countUnread(userId);
    }
    
    private MessageDTO.MessageInfo convertToMessageInfo(Message message) {
        MessageDTO.MessageInfo info = new MessageDTO.MessageInfo();
        info.setId(message.getId());
        info.setSenderId(message.getSender().getId());
        info.setSenderName(message.getSender().getNickname());
        info.setSenderAvatar(message.getSender().getAvatar());
        info.setReceiverId(message.getReceiver().getId());
        info.setReceiverName(message.getReceiver().getNickname());
        info.setReceiverAvatar(message.getReceiver().getAvatar());
        info.setContent(message.getContent());
        info.setMessageType(message.getMessageType());
        info.setIsRead(message.getIsRead());
        info.setCreatedAt(message.getCreatedAt().format(DATE_FORMATTER));
        
        if (message.getProduct() != null) {
            info.setProductId(message.getProduct().getId());
        }
        
        return info;
    }
}
