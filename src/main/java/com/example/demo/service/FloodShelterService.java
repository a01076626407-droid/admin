package com.example.demo.service;

import com.example.demo.domain.FloodShelter;
import com.example.demo.repository.FloodShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Transactional
    public void save(FloodShelter shelter) {
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("FL_" + UUID.randomUUID().toString());
        }
        floodShelterRepository.save(shelter);
    }

    @Transactional
    public void syncData() {
        System.out.println(">>> 홍수 대피소 DB 연동 실행");
    }
}