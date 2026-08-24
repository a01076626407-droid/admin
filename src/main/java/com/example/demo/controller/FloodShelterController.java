package com.example.demo.controller;

import com.example.demo.domain.FloodShelter;
import com.example.demo.repository.FloodShelterRepository;
import com.example.demo.service.FloodShelterService;
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
public class FloodShelterController {

    private final FloodShelterService floodShelterService;
    private final FloodShelterRepository floodShelterRepository;

    @GetMapping("/flood")
    public String floodPage(@RequestParam(value = "page", defaultValue = "0") int page,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").ascending());
        Page<FloodShelter> paging = floodShelterService.getShelters(keyword, pageable);

        model.addAttribute("shelters", paging.getContent());
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        return "flood";
    }

    @PostMapping("/flood/write")
    public String writeShelter(FloodShelter shelter) {
        floodShelterRepository.save(shelter);
        return "redirect:/flood";
    }

    @PostMapping("/flood/edit/{id}")
    public String updateShelter(@PathVariable("id") Long id, FloodShelter updated) {
        FloodShelter shelter = floodShelterRepository.findById(id).orElse(null);
        if (shelter != null) {
            shelter.setCtpvNm(updated.getCtpvNm());
            shelter.setSggNm(updated.getSggNm());
            shelter.setFcltNm(updated.getFcltNm());
            shelter.setDaddr(updated.getDaddr());
            shelter.setLot(updated.getLot());
            shelter.setLat(updated.getLat());
            shelter.setMngDeptNm(updated.getMngDeptNm());
            floodShelterRepository.save(shelter);
        }
        return "redirect:/flood";
    }



    @GetMapping("/flood/delete/{id}")
    public String deleteShelter(@PathVariable("id") Long id) {
        floodShelterRepository.deleteById(id);
        return "redirect:/flood";
    }
}