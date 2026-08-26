package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.service.EarthquakeShelterService;
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
public class EarthquakeShelterController {

    private final EarthquakeShelterService earthquakeShelterService;

    @GetMapping("/shelters/earthquake")
    public String getEarthquakeShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            Model model) {
        Page<EarthquakeShelter> shelters = earthquakeShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "earthquake";
    }

    @PostMapping("/earthquake/write")
    public String writeEarthquakeShelter(EarthquakeShelter shelter) {
        earthquakeShelterService.save(shelter);
        return "redirect:/shelters/earthquake";
    }

    @PostMapping("/earthquake/edit/{id}")
    public String editEarthquakeShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "lat", required = false) String latStr,
            @RequestParam(value = "lot", required = false) String lotStr,
            EarthquakeShelter shelterDto,
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
            String redirectUrl = "redirect:/shelters/earthquake";
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirectUrl += "?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            }
            return redirectUrl;
        }

        earthquakeShelterService.update(id, shelterDto);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/earthquake?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/earthquake";
    }

    // 💡 삭제 시에도 검색어(keyword) 유지 처리
    @GetMapping("/earthquake/delete/{id}")
    public String deleteEarthquakeShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "keyword", required = false) String keyword) {
        earthquakeShelterService.delete(id);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/earthquake?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/earthquake";
    }

    @PostMapping("/shelters/sync/earthquake")
    public String syncEarthquakeShelters() {
        earthquakeShelterService.syncData();
        return "redirect:/shelters/earthquake";
    }
}