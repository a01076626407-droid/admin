package com.example.demo.service;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.repository.EarthquakeShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EarthquakeShelterService {
    private final EarthquakeShelterRepository earthquakeShelterRepository;

    public Page<EarthquakeShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return earthquakeShelterRepository.findAll(pageable);
        }
        return earthquakeShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    public List<EarthquakeShelter> getAllShelters() {
        return earthquakeShelterRepository.findAll();
    }
}