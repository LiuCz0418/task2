package com.es.secondhand.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, length = 100)
    private String password;
    
    @Column(length = 50)
    private String nickname;
    
    @Column(length = 255)
    private String avatar;
    
    @Column(length = 100)
    private String email;
    
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "id_card", length = 18)
    private String idCard;
    
    @Column(name = "credit_score")
    private Integer creditScore = 100;
    
    @Column
    private Integer status = 1;

    /**
     * 用户角色：USER（普通用户）/ ADMIN（管理员）
     */
    @Column(length = 20)
    private String role = "USER";
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
