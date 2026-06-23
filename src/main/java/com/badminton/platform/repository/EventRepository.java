package com.badminton.platform.repository;

import com.badminton.platform.entity.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // basic
    List<Event> findByStatus(String status);

    // latest events
    @Query("SELECT e FROM Event e ORDER BY e.startTime DESC")
    List<Event> findLatestEvents(Pageable pageable);

    // host events
    List<Event> findByHostId(Long hostId);

    // SEARCH API (QUAN TRỌNG NHẤT)

    @Query("""
            SELECT e FROM Event e
            WHERE
            (
                LOWER(e.title) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(e.locationName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(e.sport.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(e.prefectureCode) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(e.cityCode) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(e.levelFrom) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(e.levelTo) LIKE LOWER(CONCAT('%', :q, '%'))
            )
            AND (:sport = 'ALL' OR e.sport.name = :sport)
            AND (:prefecture = '' OR e.prefectureCode = :prefecture)
            AND (:city = '' OR e.cityCode = :city)
            ORDER BY e.startTime DESC
            """)
    List<Event> searchEvents(
            @Param("q") String q,
            @Param("sport") String sport,
            @Param("prefecture") String prefecture,
            @Param("city") String city);

    @Query("""
            SELECT e FROM Event e
            WHERE e.endTime >= :start
            AND e.startTime <= :end
            """)
    Page<Event> searchByTime(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query(value = """
            SELECT * FROM events e
            WHERE
                (:lat IS NULL OR :lng IS NULL OR
                 (6371 * acos(
                     cos(radians(:lat)) * cos(radians(e.location_lat)) *
                     cos(radians(e.location_lng) - radians(:lng)) +
                     sin(radians(:lat)) * sin(radians(e.location_lat))
                 )) <= :radius)
            """, countQuery = """
            SELECT count(*) FROM events e
            WHERE
                (:lat IS NULL OR :lng IS NULL OR
                 (6371 * acos(
                     cos(radians(:lat)) * cos(radians(e.location_lat)) *
                     cos(radians(e.location_lng) - radians(:lng)) +
                     sin(radians(:lat)) * sin(radians(e.location_lat))
                 )) <= :radius)
            """, nativeQuery = true)
    Page<Event> findNearby(
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius,
            Pageable pageable);

    @Query(value = """
            SELECT * FROM events e
            WHERE
                e.end_time >= COALESCE(:start, e.end_time)
                AND e.start_time <= COALESCE(:end, e.start_time)

                AND (:prefecture = '' OR e.prefecture_code = :prefecture)
                AND (:city = '' OR e.city_code = :city)
                AND (:sport IS NULL OR e.sport_id = :sport)

                AND (
                    :keyword = '' OR
                    LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )

                AND (
                    :lat IS NULL OR :lng IS NULL OR :radius IS NULL OR
                    (
                        6371 * acos(
                            cos(radians(:lat)) * cos(radians(e.location_lat)) *
                            cos(radians(e.location_lng) - radians(:lng)) +
                            sin(radians(:lat)) * sin(radians(e.location_lat))
                        )
                    ) <= :radius
                )
            """, nativeQuery = true)
    Page<Event> searchFull(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("prefecture") String prefecture,
            @Param("city") String city,
            @Param("sport") Long sport,
            @Param("keyword") String keyword,
            @Param("lat") Double lat,
            @Param("lng") Double lng,
            @Param("radius") Double radius,
            Pageable pageable);

    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findTop3ByStartTimeBetweenOrderByStartTimeAsc(
            LocalDateTime start,
            LocalDateTime end);

    Page<Event> findByHostId(Long hostId, Pageable pageable);

    Page<Event> findByHostIdAndStartTimeAfterOrderByStartTimeAsc(
            Long hostId,
            LocalDateTime now,
            Pageable pageable);

    Page<Event> findByHostIdAndStartTimeBeforeOrderByStartTimeDesc(
            Long hostId,
            LocalDateTime now,
            Pageable pageable);

}