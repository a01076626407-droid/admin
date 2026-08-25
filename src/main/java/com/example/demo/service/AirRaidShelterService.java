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
import java.util.UUID;

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
        // 수동 할당 방식이므로 ID가 없으면 UUID로 자동 생성해서 부여
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("USER_" + UUID.randomUUID().toString());
        }
        airRaidShelterRepository.save(shelter);
    }

    @Transactional
    public void syncData() {
        try {
            System.out.println(">>> 공습 대피소 파이썬 DB 연동 실행");

            // 👉 파이썬 스크립트 실행 연동
            ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/airstrike.py");
            processBuilder.inheritIO(); // 파이썬 실행 로그가 인텔리제이 콘솔에 보이도록 설정
            Process process = processBuilder.start();

            int exitCode = process.waitFor(); // 파이썬 실행이 끝날 때까지 대기
            if (exitCode != 0) {
                throw new RuntimeException("파이썬 스크립트 실행 실패 (Exit Code: " + exitCode + ")");
            }

            System.out.println(">>> 공습 대피소 파이썬 DB 연동 완료");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("공습 DB 연동 중 오류 발생: " + e.getMessage());
        }
    }
}