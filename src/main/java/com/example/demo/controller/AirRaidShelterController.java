package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import com.example.demo.service.AirRaidShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class AirRaidShelterController {

    private final AirRaidShelterService airRaidShelterService;
    private final AirRaidShelterRepository airRaidShelterRepository;

    @GetMapping({"/", "/shelter"})
    public String shelterPage(Model model) {
        model.addAttribute("shelters", airRaidShelterService.getAllShelters());
        return "shelter";
    }

    @GetMapping("/shelter/detail/{id}")
    public String shelterDetail(@PathVariable("id") Long id, Model model) {
        AirRaidShelter shelter = airRaidShelterRepository.findById(id).orElse(null);
        model.addAttribute("shelter", shelter);
        return "shelter-detail";
    }

}