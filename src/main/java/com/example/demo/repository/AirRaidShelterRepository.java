package com.example.demo.repository;

import com.example.demo.domain.AirRaidShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirRaidShelterRepository extends JpaRepository<AirRaidShelter, Long> {
    Page<AirRaidShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);
}