package com.example.demo.controller;

import com.example.demo.domain.FloodShelter;
import com.example.demo.service.FloodShelterService;
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
public class FloodShelterController {

    private final FloodShelterService floodShelterService;

    @GetMapping("/shelters/flood")
    public String getFloodShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            Model model) {
        Page<FloodShelter> shelters = floodShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "flood";
    }

    @PostMapping("/flood/write")
    public String writeFloodShelter(FloodShelter shelter) {
        floodShelterService.save(shelter);
        return "redirect:/shelters/flood";
    }

    @PostMapping("/flood/edit/{id}")
    public String editFloodShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "lat", required = false) String latStr,
            @RequestParam(value = "lot", required = false) String lotStr,
            FloodShelter shelterDto,
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
            String redirectUrl = "redirect:/shelters/flood";
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirectUrl += "?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            }
            return redirectUrl;
        }

        floodShelterService.update(id, shelterDto);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/flood?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/flood";
    }

    // 💡 삭제 시에도 검색어(keyword) 유지 처리
    @GetMapping("/flood/delete/{id}")
    public String deleteFloodShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "keyword", required = false) String keyword) {
        floodShelterService.delete(id);
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/flood?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/flood";
    }

    @PostMapping("/shelters/sync/flood")
    public String syncFloodShelters() {
        floodShelterService.syncData();
        return "redirect:/shelters/flood";
    }
}