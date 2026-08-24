package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import com.example.demo.service.AirRaidShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final AirRaidShelterRepository airRaidShelterRepository;

    @GetMapping({"/shelter", "/shelters"})
    public String shelterPage(@RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "keyword", required = false) String keyword,
                              Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").ascending());
        Page<AirRaidShelter> paging = airRaidShelterService.getShelters(keyword, pageable);

        model.addAttribute("shelters", paging.getContent());
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        return "shelter";
    }

    @PostMapping("/shelter/write")
    public String writeShelter(AirRaidShelter shelter) {
        airRaidShelterRepository.save(shelter);
        return "redirect:/shelter";
    }

    @PostMapping("/shelter/edit/{id}")
    public String updateShelter(@PathVariable("id") Long id, AirRaidShelter updated) {
        AirRaidShelter shelter = airRaidShelterRepository.findById(id).orElse(null);
        if (shelter != null) {
            shelter.setCtpvNm(updated.getCtpvNm());
            shelter.setSggNm(updated.getSggNm());
            shelter.setFcltNm(updated.getFcltNm());
            shelter.setDaddr(updated.getDaddr());
            shelter.setLot(updated.getLot());
            shelter.setLat(updated.getLat());
            shelter.setMngDeptNm(updated.getMngDeptNm());
            airRaidShelterRepository.save(shelter);
        }
        return "redirect:/shelter";
    }

    @GetMapping("/shelter/delete/{id}")
    public String deleteShelter(@PathVariable("id") Long id) {
        airRaidShelterRepository.deleteById(id);
        return "redirect:/shelter";
    }
}