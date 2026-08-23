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
    private String fcltNm;

    @Column(name = "daddr")
    private String daddr;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lot")
    private Double lot;
}