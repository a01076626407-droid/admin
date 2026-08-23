package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "air_shelter_info")
@Getter
@Setter
@NoArgsConstructor
public class AirRaidShelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shlt_id")
    private String shltId;

    @Column(name = "ctpv_nm")
    private String ctpvNm;

    @Column(name = "sgg_nm")
    private String sggNm;

    @Column(name = "fclt_nm")
    private String fcltNm;

    @Column(name = "daddr")
    private String daddr;

    @Column(name = "lot")
    private Double lot;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "mng_dept_nm")
    private String mngDeptNm;
}