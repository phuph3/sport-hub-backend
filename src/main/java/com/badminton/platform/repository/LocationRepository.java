package com.badminton.platform.repository;

import com.badminton.platform.entity.Location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCityCode(String cityCode);

    Optional<Location> findByCityJaAndPrefectureJa(String cityJa, String prefectureJa);

    @Query("""
            SELECT l FROM Location l
            WHERE l.prefectureJa = :prefectureJa
            AND (
                l.cityJa LIKE %:wardJa%
                OR l.cityJa LIKE %:cityJa%
            )
            """)
    Optional<Location> findByJaFlexible(
            @Param("prefectureJa") String prefectureJa,
            @Param("cityJa") String cityJa,
            @Param("wardJa") String wardJa);

}