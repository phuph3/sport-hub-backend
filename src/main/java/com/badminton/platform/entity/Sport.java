package com.badminton.platform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "sports")
@Getter
@Setter
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "name_ja")
    private String nameJa;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_vi")
    private String nameVi;

}
