package com.example.demo.repository;

import com.example.demo.domain.AirRaidShelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirRaidShelterRepository extends JpaRepository<AirRaidShelter, Long> {
}