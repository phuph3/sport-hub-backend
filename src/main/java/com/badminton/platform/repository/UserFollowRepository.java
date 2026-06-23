package com.badminton.platform.repository;

import com.badminton.platform.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Modifying
    @Transactional
    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);

    long countByFollowingId(Long followingId); // followers

    long countByFollowerId(Long followerId); // following

    @Query("SELECT f.followingId FROM UserFollow f WHERE f.followerId = :userId")
    List<Long> findFollowingIds(@Param("userId") Long userId);

    @Query("SELECT f.followerId FROM UserFollow f WHERE f.followingId = :userId")
    List<Long> findFollowerIds(@Param("userId") Long userId);

}