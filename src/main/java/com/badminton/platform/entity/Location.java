package com.badminton.platform.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prefecture_code")
    private String prefectureCode;

    @Column(name = "prefecture_ja")
    private String prefectureJa;

    @Column(name = "prefecture_en")
    private String prefectureEn;

    @Column(name = "prefecture_vi")
    private String prefectureVi;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "city_ja")
    private String cityJa;

    @Column(name = "city_en")
    private String cityEn;

    @Column(name = "city_vi")
    private String cityVi;
}