package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import com.example.demo.service.AirRaidShelterService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AdminShelterController {

    private final AirRaidShelterService airRaidShelterService;
    private final AirRaidShelterRepository airRaidShelterRepository;

    // 1. 관리자 전용 대피소 목록 및 관리 페이지
    @GetMapping("/admin/shelters")
    public String adminShelterList(Model model, HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (!"super".equals(loginUser)) {
            return "redirect:/shelter";
        }

        model.addAttribute("shelters", airRaidShelterService.getAllShelters());
        return "admin-shelter-list";
    }

    // 2. 대피소 등록 페이지 이동
    @GetMapping("/admin/shelters/write")
    public String adminWritePage(HttpSession session) {
        if (!"super".equals(session.getAttribute("loginUser"))) {
            return "redirect:/shelter";
        }
        return "admin-shelter-write";
    }

    // 3. 대피소 등록 처리
    @PostMapping("/admin/shelters/write")
    public String adminWriteShelter(AirRaidShelter shelter, HttpSession session) {
        if (!"super".equals(session.getAttribute("loginUser"))) {
            return "redirect:/shelter";
        }
        airRaidShelterRepository.save(shelter);
        return "redirect:/admin/shelters";
    }

    // 4. 대피소 수정 페이지 이동
    @GetMapping("/admin/shelters/edit/{id}")
    public String adminEditPage(@PathVariable("id") Long id, Model model, HttpSession session) {
        if (!"super".equals(session.getAttribute("loginUser"))) {
            return "redirect:/shelter";
        }

        AirRaidShelter shelter = airRaidShelterRepository.findById(id).orElse(null);
        if (shelter == null) {
            return "redirect:/admin/shelters";
        }

        model.addAttribute("shelter", shelter);
        return "admin-shelter-edit";
    }

    // 5. 대피소 수정 처리 (실제 필드명인 fcltNm과 daddr 사용)
    @PostMapping("/admin/shelters/edit/{id}")
    public String adminUpdateShelter(@PathVariable("id") Long id, AirRaidShelter updated, HttpSession session) {
        if (!"super".equals(session.getAttribute("loginUser"))) {
            return "redirect:/shelter";
        }

        AirRaidShelter shelter = airRaidShelterRepository.findById(id).orElse(null);
        if (shelter != null) {
            shelter.setFcltNm(updated.getFcltNm());
            shelter.setDaddr(updated.getDaddr());
            airRaidShelterRepository.save(shelter);
        }

        return "redirect:/admin/shelters";
    }

    // 6. 대피소 삭제 처리
    @GetMapping("/admin/shelters/delete/{id}")
    public String adminDeleteShelter(@PathVariable("id") Long id, HttpSession session) {
        if (!"super".equals(session.getAttribute("loginUser"))) {
            return "redirect:/shelter";
        }

        airRaidShelterRepository.deleteById(id);
        return "redirect:/admin/shelters";
    }
}