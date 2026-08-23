package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "flood_shelter")
@Getter
@Setter
@NoArgsConstructor
public class FloodShelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fclt_nm")
    private String fcltNm; // 시설명

    @Column(name = "daddr")
    private String daddr; // 주소

    @Column(name = "mng_dept_nm")
    private String mngDeptNm; // 관리 부서

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lot")
    private Double lot;
}