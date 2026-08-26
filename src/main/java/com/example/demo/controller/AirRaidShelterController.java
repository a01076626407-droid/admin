package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.service.AirRaidShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class AirRaidShelterController {

    private final AirRaidShelterService airRaidShelterService;

    @GetMapping("/shelters/air")
    public String getAirRaidShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            Model model) {
        Page<AirRaidShelter> shelters = airRaidShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "shelter";
    }

    @PostMapping("/shelter/write")
    public String writeAirRaidShelter(AirRaidShelter shelter) {
        airRaidShelterService.save(shelter);
        return "redirect:/shelters/air";
    }

    @PostMapping("/shelter/edit/{id}")
    public String editAirRaidShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "lat", required = false) String latStr,
            @RequestParam(value = "lot", required = false) String lotStr,
            AirRaidShelter shelterDto,
            @RequestParam(value = "keyword", required = false) String keyword,
            RedirectAttributes redirectAttributes) {
        try {
            if (latStr != null && !latStr.trim().isEmpty()) {
                Double.parseDouble(latStr);
            }
            if (lotStr != null && !lotStr.trim().isEmpty()) {
                Double.parseDouble(lotStr);
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "위도와 경도에는 숫자만 입력해야 합니다!");
            String redirectUrl = "redirect:/shelters/air";
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirectUrl += "?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            }
            return redirectUrl;
        }

        airRaidShelterService.update(id, shelterDto);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/air?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/air";
    }

    // 💡 삭제 시에도 검색어(keyword) 유지 처리
    @GetMapping("/shelter/delete/{id}")
    public String deleteAirRaidShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "keyword", required = false) String keyword) {
        airRaidShelterService.delete(id);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/air?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/air";
    }

    @PostMapping("/shelters/sync/air")
    public String syncAirRaidShelters() {
        airRaidShelterService.syncData();
        return "redirect:/shelters/air";
    }
}