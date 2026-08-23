package com.example.demo.service;

import com.example.demo.domain.FloodShelter;
import com.example.demo.repository.FloodShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FloodShelterService {
    private final FloodShelterRepository repository;

    public List<FloodShelter> getAllShelters() {
        return repository.findAll();
    }
}