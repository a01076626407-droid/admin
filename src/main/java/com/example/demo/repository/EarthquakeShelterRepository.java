package com.example.demo.repository;

import com.example.demo.domain.EarthquakeShelter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EarthquakeShelterRepository extends JpaRepository<EarthquakeShelter, Long> {
    List<EarthquakeShelter> findByDaddrContaining(String keyword);
}