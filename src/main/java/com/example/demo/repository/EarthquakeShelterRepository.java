package com.example.demo.repository;

import com.example.demo.domain.EarthquakeShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EarthquakeShelterRepository extends JpaRepository<EarthquakeShelter, Long> {
    Page<EarthquakeShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);
    Optional<EarthquakeShelter> findByShltId(String shltId);
    void deleteByShltId(String shltId);
}