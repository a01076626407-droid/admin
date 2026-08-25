package com.example.demo.repository;

import com.example.demo.domain.FloodShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FloodShelterRepository extends JpaRepository<FloodShelter, String> { // 👈 Long을 String으로 변경!

    Page<FloodShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);

    Optional<FloodShelter> findByShltId(String shltId);

    void deleteByShltId(String shltId);
}