package com.example.demo.service;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirRaidShelterService {

    private final AirRaidShelterRepository airRaidShelterRepository;

    public Page<AirRaidShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return airRaidShelterRepository.findAll(pageable);
        }
        return airRaidShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    public List<AirRaidShelter> getAllShelters() {
        return airRaidShelterRepository.findAll();
    }
}