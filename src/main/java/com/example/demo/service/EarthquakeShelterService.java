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

    public Page<EarthquakeShelter> getShelters(Pageable pageable) {
        return earthquakeShelterRepository.findAll(pageable);
    }

    // 🟢 관리자 페이지용 전체 조회 메서드 추가
    public List<EarthquakeShelter> getAllShelters() {
        return earthquakeShelterRepository.findAll();
    }
}