package com.es.secondhand.service;

import com.es.secondhand.dto.UserDTO;
import com.es.secondhand.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    
    UserDTO.LoginResponse register(UserDTO.RegisterRequest request);
    
    UserDTO.LoginResponse login(UserDTO.LoginRequest request);
    
    UserDTO.UserInfo getUserInfo(Integer userId);
    
    UserDTO.UserInfo updateUser(Integer userId, UserDTO.UpdateRequest request);
    
    User getCurrentUser();
    
    void updateCreditScore(Integer userId, int delta);

    /** 获取待审核注册用户列表（status=0） */
    Page<UserDTO.UserAuditInfo> getPendingUsers(Pageable pageable);

    /** 管理员审核注册：APPROVE → status=1，REJECT → status=2 */
    void auditUser(Integer userId, UserDTO.UserAuditRequest request);
}
