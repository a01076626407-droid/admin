package com.example.demo.repository;

import com.example.demo.domain.AirRaidShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AirRaidShelterRepository extends JpaRepository<AirRaidShelter, Long> {
    Page<AirRaidShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);
    Optional<AirRaidShelter> findByShltId(String shltId);
    void deleteByShltId(String shltId);
}