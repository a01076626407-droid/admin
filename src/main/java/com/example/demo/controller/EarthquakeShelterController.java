package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.service.EarthquakeShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/earthquake")
@RequiredArgsConstructor
public class EarthquakeShelterController {
    private final EarthquakeShelterService service;

    @GetMapping("/shelters")
    public List<EarthquakeShelter> getShelters() {
        return service.getAllShelters().stream().limit(100).toList();
    }
}