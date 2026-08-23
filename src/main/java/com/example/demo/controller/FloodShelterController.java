package com.example.demo.controller;

import com.example.demo.domain.FloodShelter;
import com.example.demo.repository.FloodShelterRepository;
import com.example.demo.service.FloodShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class FloodShelterController {

    private final FloodShelterService floodShelterService;
    private final FloodShelterRepository floodShelterRepository;

    @GetMapping("/flood")
    public String floodPage(Model model) {
        model.addAttribute("shelters", floodShelterService.getAllShelters());
        return "flood";
    }

    @GetMapping("/flood/detail/{id}")
    public String floodDetail(@PathVariable("id") Long id, Model model) {
        FloodShelter shelter = floodShelterRepository.findById(id).orElse(null);
        model.addAttribute("shelter", shelter);
        return "flood-detail";
    }
}