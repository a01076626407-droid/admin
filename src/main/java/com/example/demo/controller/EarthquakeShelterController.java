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
import org.springframework.web.bind.annotation.PathVariable;
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
        // "earthquake"; 뒤에 잘못 입력된 'a' 오타 수정
        return "earthquake";
    }

    @PostMapping("/shelters/sync/earthquake")
    public String syncEarthquakeShelters() {
        earthquakeShelterService.syncData();
        return "redirect:/shelters/earthquake";
    }

    @PostMapping("/earthquake/write")
    public String writeEarthquakeShelter(EarthquakeShelter shelter) {
        earthquakeShelterService.save(shelter);
        return "redirect:/shelters/earthquake";
    }

    @PostMapping("/earthquake/edit/{id}")
    public String updateEarthquakeShelter(@PathVariable("id") String id, EarthquakeShelter shelter) {
        earthquakeShelterService.update(id, shelter);
        return "redirect:/shelters/earthquake";
    }

    @GetMapping("/earthquake/delete/{id}")
    public String deleteEarthquakeShelter(@PathVariable("id") String id) {
        earthquakeShelterService.delete(id);
        return "redirect:/shelters/earthquake";
    }
}