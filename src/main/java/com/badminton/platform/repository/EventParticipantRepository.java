package com.badminton.platform.repository;

import com.badminton.platform.dto.ParticipantDTO;
import com.badminton.platform.entity.EventParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    // Spring Data JPA sẽ tự động tạo các hàm như save(), findAll(), findById() cho
    // bạn!
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    List<EventParticipant> findByUserId(Long userId);

    List<EventParticipant> findByEventIdAndStatus(Long eventId, String status);

    long countByEventIdAndStatus(Long eventId, String status);

    EventParticipant findByEventIdAndUserId(Long eventId, Long userId);

    List<EventParticipant> findByEventIdAndStatusOrderByCreatedAtAsc(Long eventId, String status);

    List<EventParticipant> findByEventId(Long eventId);

    @Query("""
              SELECT new com.badminton.platform.dto.ParticipantDTO(
                u.id,
                u.nickname,
                u.fullname,
                u.avatarUrl
              )
              FROM EventParticipant ep
              JOIN ep.user u
              WHERE ep.event.id = :eventId
                AND ep.status = :status
            """)
    List<ParticipantDTO> findParticipantsByEventAndStatus(
            Long eventId,
            String status);
}