package com.es.secondhand.controller;

import com.es.secondhand.dto.ApiResponse;
import com.es.secondhand.dto.MessageDTO;
import com.es.secondhand.entity.User;
import com.es.secondhand.service.MessageService;
import com.es.secondhand.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "消息管理", description = "聊天消息的发送和接收")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "发送消息")
    public ApiResponse<MessageDTO.MessageInfo> sendMessage(@Valid @RequestBody MessageDTO.SendRequest request) {
        try {
            User user = userService.getCurrentUser();
            MessageDTO.MessageInfo messageInfo = messageService.sendMessage(user.getId(), request);
            return ApiResponse.success("发送成功", messageInfo);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
    
    @GetMapping("/conversation/{userId}")
    @Operation(summary = "获取与某用户的聊天记录")
    public ApiResponse<Page<MessageDTO.MessageInfo>> getConversation(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            User currentUser = userService.getCurrentUser();
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<MessageDTO.MessageInfo> messages = messageService.getConversation(currentUser.getId(), userId, pageRequest);
            
            messageService.markAsRead(currentUser.getId(), userId);
            
            return ApiResponse.success(messages);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/conversations")
    @Operation(summary = "获取会话列表")
    public ApiResponse<List<MessageDTO.ConversationInfo>> getConversationList() {
        try {
            User user = userService.getCurrentUser();
            List<MessageDTO.ConversationInfo> conversations = messageService.getConversationList(user.getId());
            return ApiResponse.success(conversations);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/unread")
    @Operation(summary = "获取未读消息数量")
    public ApiResponse<Long> getUnreadCount() {
        try {
            User user = userService.getCurrentUser();
            Long count = messageService.getUnreadCount(user.getId());
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error(401, e.getMessage());
        }
    }
    
    @PostMapping("/read/{senderId}")
    @Operation(summary = "标记消息为已读")
    public ApiResponse<Void> markAsRead(@PathVariable Integer senderId) {
        try {
            User user = userService.getCurrentUser();
            messageService.markAsRead(user.getId(), senderId);
            return ApiResponse.success("标记成功", null);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }
}
