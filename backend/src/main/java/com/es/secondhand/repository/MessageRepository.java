package com.es.secondhand.repository;

import com.es.secondhand.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId1 AND m.receiver.id = :userId2) OR (m.sender.id = :userId2 AND m.receiver.id = :userId1) ORDER BY m.createdAt ASC")
    Page<Message> findConversation(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2, Pageable pageable);
    
    @Query("SELECT DISTINCT CASE WHEN m.sender.id = :userId THEN m.receiver.id ELSE m.sender.id END FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    List<Integer> findConversationUserIds(@Param("userId") Integer userId);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = 1 WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = 0")
    void markAsRead(@Param("receiverId") Integer receiverId, @Param("senderId") Integer senderId);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId AND m.isRead = 0")
    Long countUnread(@Param("userId") Integer userId);
}
