package com.example.demo.repository;

import com.example.demo.domain.FloodShelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloodShelterRepository extends JpaRepository<FloodShelter, Long> {
}