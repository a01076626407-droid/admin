package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "airstrike")
@Getter
@Setter
@NoArgsConstructor
public class AirRaidShelter {

    @Id
    @Column(name = "shlt_id") // 실제 DB의 PK 컬럼명과 매핑
    private String shltId;      // 타입도 DB에 맞춰 String으로 지정 (IDENTITY 제거)

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