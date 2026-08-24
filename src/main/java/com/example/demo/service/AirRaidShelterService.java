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

    public Page<AirRaidShelter> getShelters(Pageable pageable) {
        return airRaidShelterRepository.findAll(pageable);
    }

    // 🟢 관리자 페이지용 전체 조회 메서드 추가
    public List<AirRaidShelter> getAllShelters() {
        return airRaidShelterRepository.findAll();
    }
}