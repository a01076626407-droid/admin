package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.repository.EarthquakeShelterRepository;
import com.example.demo.service.EarthquakeShelterService;
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
public class EarthquakeShelterController {

    private final EarthquakeShelterService earthquakeShelterService;
    private final EarthquakeShelterRepository earthquakeShelterRepository;

    @GetMapping("/earthquake")
    public String earthquakePage(@RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by("id").ascending());
        Page<EarthquakeShelter> paging = earthquakeShelterService.getShelters(keyword, pageable);

        model.addAttribute("shelters", paging.getContent());
        model.addAttribute("paging", paging);
        model.addAttribute("keyword", keyword);
        return "earthquake";
    }

    @PostMapping("/earthquake/write")
    public String writeShelter(EarthquakeShelter shelter) {
        earthquakeShelterRepository.save(shelter);
        return "redirect:/earthquake";
    }

    @PostMapping("/earthquake/edit/{id}")
    public String updateShelter(@PathVariable("id") Long id, EarthquakeShelter updated) {
        EarthquakeShelter shelter = earthquakeShelterRepository.findById(id).orElse(null);
        if (shelter != null) {
            shelter.setCtpvNm(updated.getCtpvNm());
            shelter.setSggNm(updated.getSggNm());
            shelter.setFcltNm(updated.getFcltNm());
            shelter.setDaddr(updated.getDaddr());
            shelter.setLot(updated.getLot());
            shelter.setLat(updated.getLat());
            shelter.setMngDeptNm(updated.getMngDeptNm());
            earthquakeShelterRepository.save(shelter);
        }
        return "redirect:/earthquake";
    }

    @GetMapping("/earthquake/delete/{id}")
    public String deleteShelter(@PathVariable("id") Long id) {
        earthquakeShelterRepository.deleteById(id);
        return "redirect:/earthquake";
    }
}