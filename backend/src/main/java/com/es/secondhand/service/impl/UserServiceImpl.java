package com.es.secondhand.service.impl;

import com.es.secondhand.config.JwtUtil;
import com.es.secondhand.dto.UserDTO;
import com.es.secondhand.entity.User;
import com.es.secondhand.repository.UserRepository;
import com.es.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    @Transactional
    public UserDTO.LoginResponse register(UserDTO.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdCard(request.getIdCard());
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + request.getUsername());
        user.setRole("USER");
        user.setStatus(0);  // 0=待审核，注册后需管理员审核
        
        user = userRepository.save(user);

        // 注册后需审核，不生成 token，前端凭 code=200 判断注册成功即可
        UserDTO.LoginResponse response = new UserDTO.LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setToken(null);   // 审核通过前不可登录
        response.setRole(user.getRole());

        return response;
    }
    
    @Override
    public UserDTO.LoginResponse login(UserDTO.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号正在审核中，请等待管理员审核");
        }
        if (user.getStatus() != 1) {
            throw new RuntimeException("账号已被禁用");
        }
        
        UserDTO.LoginResponse response = new UserDTO.LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setAvatar(user.getAvatar());
        response.setToken(jwtUtil.generateToken(user.getId(), user.getUsername()));
        response.setRole(user.getRole());
        
        return response;
    }
    
    @Override
    public UserDTO.UserInfo getUserInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return convertToUserInfo(user);
    }
    
    @Override
    @Transactional
    public UserDTO.UserInfo updateUser(Integer userId, UserDTO.UpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        
        user = userRepository.save(user);
        return convertToUserInfo(user);
    }
    
    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User) {
            return (User) principal;
        }
        throw new RuntimeException("未登录");
    }
    
    @Override
    @Transactional
    public void updateCreditScore(Integer userId, int delta) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        int newScore = user.getCreditScore() + delta;
        newScore = Math.max(0, Math.min(100, newScore));
        user.setCreditScore(newScore);
        userRepository.save(user);
    }

    @Override
    public org.springframework.data.domain.Page<UserDTO.UserAuditInfo> getPendingUsers(
            org.springframework.data.domain.Pageable pageable) {
        return userRepository.findByStatus(0, pageable)
                .map(this::convertToAuditInfo);
    }

    @Override
    @Transactional
    public void auditUser(Integer userId, UserDTO.UserAuditRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (user.getStatus() != 0) {
            throw new RuntimeException("该用户不在待审核状态");
        }
        if ("APPROVE".equalsIgnoreCase(request.getAction())) {
            user.setStatus(1);
        } else {
            user.setStatus(2);
        }
        userRepository.save(user);
    }

    private UserDTO.UserAuditInfo convertToAuditInfo(User user) {
        UserDTO.UserAuditInfo info = new UserDTO.UserAuditInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setPhoneNumber(user.getPhoneNumber());
        info.setEmail(user.getEmail());
        info.setIdCard(user.getIdCard());   // 管理员看原始身份证号
        info.setCreatedAt(user.getCreatedAt().format(DATE_FORMATTER));
        return info;
    }
    
    private UserDTO.UserInfo convertToUserInfo(User user) {
        UserDTO.UserInfo info = new UserDTO.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setAvatar(user.getAvatar());
        info.setEmail(user.getEmail());
        info.setPhoneNumber(user.getPhoneNumber());
        info.setIdCard(maskIdCard(user.getIdCard()));
        info.setCreditScore(user.getCreditScore());
        info.setCreditLevel(calcCreditLevel(user.getCreditScore()));
        info.setCreatedAt(user.getCreatedAt().format(DATE_FORMATTER));
        info.setRole(user.getRole());
        return info;
    }

    /**
     * 身份证脱敏：保留前3位和后4位，中间用 * 替代
     * 例：310116199001011234 → 310***********1234
     */
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) return idCard;
        return idCard.substring(0, 3)
                + "*".repeat(idCard.length() - 7)
                + idCard.substring(idCard.length() - 4);
    }
    
    private String calcCreditLevel(Integer score) {
        if (score == null) return "信誉一般";
        if (score >= 90) return "信誉极佳";
        if (score >= 80) return "信誉优秀";
        if (score >= 70) return "信誉良好";
        if (score >= 60) return "信誉一般";
        return "信誉不佳";
    }
}
