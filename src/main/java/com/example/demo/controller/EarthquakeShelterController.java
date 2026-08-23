package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.repository.EarthquakeShelterRepository;
import com.example.demo.service.EarthquakeShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class EarthquakeShelterController {

    private final EarthquakeShelterService earthquakeShelterService;
    private final EarthquakeShelterRepository earthquakeShelterRepository;

    @GetMapping("/earthquake")
    public String earthquakePage(Model model) {
        model.addAttribute("shelters", earthquakeShelterService.getAllShelters());
        return "earthquake";
    }

    @GetMapping("/earthquake/detail/{id}")
    public String earthquakeDetail(@PathVariable("id") Long id, Model model) {
        EarthquakeShelter shelter = earthquakeShelterRepository.findById(id).orElse(null);
        model.addAttribute("shelter", shelter);
        return "earthquake-detail";
    }
}