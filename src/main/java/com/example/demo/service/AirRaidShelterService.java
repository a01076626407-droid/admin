package com.example.demo.service;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirRaidShelterService {

    private final AirRaidShelterRepository airRaidShelterRepository;

    public List<AirRaidShelter> getAllShelters() {
        return airRaidShelterRepository.findAll();
    }
}