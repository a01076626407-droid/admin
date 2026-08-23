package com.example.demo.service;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.repository.EarthquakeShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EarthquakeShelterService {
    private final EarthquakeShelterRepository repository;

    public List<EarthquakeShelter> getAllShelters() {
        return repository.findAll();
    }
}