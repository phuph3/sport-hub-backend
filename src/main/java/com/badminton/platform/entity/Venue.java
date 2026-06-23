package com.badminton.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "venues")
@Getter @Setter
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;

    @Column(name = "map_url")
    private String mapUrl;

    private Double lat;

    private Double lng;

    // PostgreSQL int[]  (cần column sport_ids kiểu integer[])
    @Column(name = "sport_ids", columnDefinition = "integer[]")
    private Integer[] sportIds;
}