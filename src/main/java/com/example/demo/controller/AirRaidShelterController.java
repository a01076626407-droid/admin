package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.service.AirRaidShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AirRaidShelterController {

    private final AirRaidShelterService airRaidShelterService;

    @GetMapping({"/shelter", "/shelters/air"})
    public String airShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        Page<AirRaidShelter> shelters = airRaidShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "shelter";
    }



    @PostMapping("/shelters/sync/air")
    public String syncAirShelters() {
        airRaidShelterService.syncData();
        return "redirect:/shelters/air";
    }

    @PostMapping("/shelter/write")
    public String writeAirShelter(AirRaidShelter shelter) {
        airRaidShelterService.save(shelter);
        return "redirect:/shelters/air";
    }

    @PostMapping("/shelter/edit/{id}")
    public String updateAirShelter(@PathVariable("id") String id, AirRaidShelter shelter) {
        airRaidShelterService.update(id, shelter);
        return "redirect:/shelters/air";
    }

    @GetMapping("/shelter/delete/{id}")
    public String deleteAirShelter(@PathVariable("id") String id) {
        airRaidShelterService.delete(id);
        return "redirect:/shelters/air";
    }
}