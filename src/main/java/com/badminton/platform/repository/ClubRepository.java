package com.badminton.platform.repository;

import com.badminton.platform.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    // Spring Data JPA sẽ tự động tạo các hàm như save(), findAll(), findById() cho bạn!
}