package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.service.EarthquakeShelterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/earthquake")
public class EarthquakeShelterController {
    private final EarthquakeShelterService earthquakeShelterService;

    public EarthquakeShelterController(EarthquakeShelterService earthquakeShelterService) {
        this.earthquakeShelterService = earthquakeShelterService;
    }

    @GetMapping("/test")
    public Map<String,Object> testEarthquake(){
        return Map.of("status","success") ;
    }
    @GetMapping("/shelters")
    public List<EarthquakeShelter> getShelter(){
        return earthquakeShelterService.getAllShelters();
    }
}
