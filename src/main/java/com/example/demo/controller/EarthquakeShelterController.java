package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.service.EarthquakeShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class EarthquakeShelterController {

    private final EarthquakeShelterService earthquakeShelterService;

    @GetMapping("/shelters/earthquake")
    public String earthquakeShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        Page<EarthquakeShelter> shelters = earthquakeShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "earthquake";
    }

    @PostMapping("/shelters/sync/earthquake")
    public String syncEarthquakeShelters() {
        earthquakeShelterService.syncData();
        return "redirect:/shelters/earthquake";
    }

    // 👉 지진 대피소 등록 처리
    @PostMapping("/earthquake/write")
    public String writeEarthquakeShelter(EarthquakeShelter shelter) {
        earthquakeShelterService.save(shelter);
        return "redirect:/shelters/earthquake";
    }
}