package com.example.demo.repository;

import com.example.demo.domain.FloodShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FloodShelterRepository extends JpaRepository<FloodShelter, Long> {
    Page<FloodShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);
}