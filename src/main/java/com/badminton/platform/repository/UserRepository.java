package com.badminton.platform.repository;

import com.badminton.platform.entity.FavoriteEvent;
import com.badminton.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    List<User> findAllByLevel(String level);

}