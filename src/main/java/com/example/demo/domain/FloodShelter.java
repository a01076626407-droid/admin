package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "flood_shelter")
public class FloodShelter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ctpvNm;   // 시도명
    private String sggNm;    // 시군구명
    private String fcltNm;   // 대피소 이름
    private String daddr;    // 주소
    private String lot;      // 경도
    private String lat;      // 위도
    private String mngDeptNm;// 관리부서명
}