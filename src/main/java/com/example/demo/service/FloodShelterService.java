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
        try {
            System.out.println(">>> 홍수 대피소 파이썬 DB 연동 실행");

            // 👉 홍수 대피소 파이썬 스크립트 실행 연동 (엑셀 데이터를 MySQL에 적재하는 스크립트 파일명으로 지정)
            ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/excel_to_mysql.py");
            processBuilder.inheritIO(); // 파이썬 실행 로그가 인텔리제이 콘솔에 출력되도록 설정
            Process process = processBuilder.start();

            int exitCode = process.waitFor(); // 파이썬 실행 완료 대기
            if (exitCode != 0) {
                throw new RuntimeException("홍수 파이썬 스크립트 실행 실패 (Exit Code: " + exitCode + ")");
            }

            System.out.println(">>> 홍수 대피소 파이썬 DB 연동 완료");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("홍수 DB 연동 중 오류 발생: " + e.getMessage());
        }
    }
}