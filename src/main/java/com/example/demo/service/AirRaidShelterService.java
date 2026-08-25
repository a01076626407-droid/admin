package com.example.demo.service;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID; // 👉 UUID 추가

@Service
@RequiredArgsConstructor
public class AirRaidShelterService {

    private final AirRaidShelterRepository airRaidShelterRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<AirRaidShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return airRaidShelterRepository.findAll(pageable);
        }
        return airRaidShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    public List<AirRaidShelter> getAllShelters() {
        return airRaidShelterRepository.findAll();
    }

    @Transactional
    public void save(AirRaidShelter shelter) {
        // 👉 수동 할당 방식이므로 ID가 없으면 UUID로 자동 생성해서 부여
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("USER_" + UUID.randomUUID().toString());
        }
        airRaidShelterRepository.save(shelter);
    }

    @Transactional
    public void syncData() {
        try {
            System.out.println(">>> 공습 대피소 DB 연동 실행");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("공습 DB 연동 중 오류 발생: " + e.getMessage());
        }
    }
}