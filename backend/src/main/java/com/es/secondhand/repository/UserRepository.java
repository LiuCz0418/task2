package com.es.secondhand.repository;

import com.es.secondhand.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    Optional<User> findByEmail(String email);

    Optional<User> findFirstByRole(String role);
    
    boolean existsByUsername(String username);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);

    /** 查询待审核（status=0）的注册用户列表 */
    Page<User> findByStatus(Integer status, Pageable pageable);
}
