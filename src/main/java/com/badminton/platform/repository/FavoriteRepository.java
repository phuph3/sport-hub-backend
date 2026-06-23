package com.badminton.platform.repository;

import com.badminton.platform.entity.FavoriteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<FavoriteEvent, Long> {

    List<FavoriteEvent> findByUserId(Long userId);

    Optional<FavoriteEvent> findByUserIdAndEventId(Long userId, Long eventId);

    void deleteByUserIdAndEventId(Long userId, Long eventId);
    
}

