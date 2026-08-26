package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "flood")
public class FloodShelter {

    @Id
    @Column(name = "shlt_id", length = 255, nullable = false)
    private String shltId;      // 수동으로 생성하는 문자열 ID (FL_...)

    @Column(name = "ctpv_nm")
    private String ctpvNm;   // 시도명

    @Column(name = "sgg_nm")
    private String sggNm;    // 시군구명

    @Column(name = "fclt_nm")
    private String fcltNm;   // 대피소 이름

    @Column(name = "daddr")
    private String daddr;    // 주소

    @Column(name = "lot")
    private Double lot;      // 경도

    @Column(name = "lat")
    private Double lat;      // 위도

    @Column(name = "mng_dept_nm")
    private String mngDeptNm;// 관리부서명
}