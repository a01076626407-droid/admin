package com.example.demo.repository;

import com.example.demo.domain.AirRaidShelter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AirRaidShelterRepository extends JpaRepository<AirRaidShelter, Long> {
    // 실제 엔티티 필드명인 daddr을 사용하여 주소 검색 메서드 생성
    List<AirRaidShelter> findByDaddrContaining(String keyword);
}