package com.example.demo.service;

import com.example.demo.domain.FloodShelter;
import com.example.demo.repository.FloodShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FloodShelterService {
    private final FloodShelterRepository floodShelterRepository;

    public Page<FloodShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return floodShelterRepository.findAll(pageable);
        }
        return floodShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    public List<FloodShelter> getAllShelters() {
        return floodShelterRepository.findAll();
    }
}