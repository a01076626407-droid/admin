package com.example.demo.controller;

import com.example.demo.domain.FloodShelter;
import com.example.demo.service.FloodShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flood")
@RequiredArgsConstructor
public class FloodShelterController {
    private final FloodShelterService service;

    @GetMapping("/shelters")
    public List<FloodShelter> getShelters() {
        return service.getAllShelters().stream().limit(100).toList();
    }
}