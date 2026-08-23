package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.service.AirRaidShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airraid")
@RequiredArgsConstructor
public class AirRaidShelterController {

    private final AirRaidShelterService airRaidShelterService;

    @GetMapping("/shelters")
    public List<AirRaidShelter> getAllShelters() {
        return airRaidShelterService.getAllShelters().stream().limit(100).toList();
    }

}