package com.example.demo.service;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.repository.EarthquakeShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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

    @Transactional
    public void save(EarthquakeShelter shelter) {
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("EQ_" + UUID.randomUUID().toString());
        }
        earthquakeShelterRepository.save(shelter);
    }

    @Transactional
    public void syncData() {
        System.out.println(">>> 지진 대피소 DB 연동 실행");
    }
}