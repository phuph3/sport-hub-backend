package com.badminton.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.badminton.platform.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
            "WHERE n.userId = :userId OR n.isGlobal = true " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findAllForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(n) FROM Notification n " +
            "WHERE (n.userId = :userId OR n.isGlobal = true) " +
            "AND n.isRead = false")
    long countUnreadForUser(@Param("userId") Long userId);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
}