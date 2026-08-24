package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FloodShelterControllerTest {

    @Autowired
    private FloodShelterController floodShelterController;

    @Test
    void contextLoads() {
        // 컨트롤러가 스프링 컨테이너에 정상적으로 등록되었는지 테스트
        assertThat(floodShelterController).isNotNull();
    }
}