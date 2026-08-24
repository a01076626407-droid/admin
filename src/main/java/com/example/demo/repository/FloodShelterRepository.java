package com.example.demo.repository;

import com.example.demo.domain.FloodShelter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FloodShelterRepository extends JpaRepository<FloodShelter, Long> {
    List<FloodShelter> findByDaddrContaining(String keyword);
}