package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "earthquake_shelter")
@Getter
@Setter
@NoArgsConstructor
public class EarthquakeShelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fclt_nm")
    private String fcltNm; // 시설명

    @Column(name = "daddr")
    private String daddr; // 주소

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lot")
    private Double lot;
}