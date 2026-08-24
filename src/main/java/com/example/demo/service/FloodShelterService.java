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

    public Page<FloodShelter> getShelters(Pageable pageable) {
        return floodShelterRepository.findAll(pageable);
    }

    // 🟢 관리자 페이지용 전체 조회 메서드 추가
    public List<FloodShelter> getAllShelters() {
        return floodShelterRepository.findAll();
    }
}