package com.example.demo.controller;

import com.example.demo.domain.FloodShelter;
import com.example.demo.service.FloodShelterService;
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

import java.util.UUID; // 👈 UUID 임포트 추가

@Controller
@RequiredArgsConstructor
public class FloodShelterController {

    private final FloodShelterService floodShelterService;

    @GetMapping("/shelters/flood")
    public String floodShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        Page<FloodShelter> shelters = floodShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "flood";
    }

    @PostMapping("/shelters/sync/flood")
    public String syncFloodShelters() {
        floodShelterService.syncData();
        return "redirect:/shelters/flood";
    }

    @PostMapping("/flood/write")
    public String writeFloodShelter(FloodShelter shelter) {
        // 👇 핵심: 등록할 때 ID가 없다면 FL_ + UUID 조합으로 고유 ID를 강제로 부여합니다!
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("FL_" + UUID.randomUUID().toString());
        }

        floodShelterService.save(shelter);
        return "redirect:/shelters/flood";
    }

    @PostMapping("/flood/edit/{id}")
    public String updateFloodShelter(@PathVariable("id") String id, FloodShelter shelter) {
        floodShelterService.update(id, shelter);
        return "redirect:/shelters/flood";
    }

    @GetMapping("/flood/delete/{id}")
    public String deleteFloodShelter(@PathVariable("id") String id) {
        floodShelterService.delete(id);
        return "redirect:/shelters/flood";
    }
}