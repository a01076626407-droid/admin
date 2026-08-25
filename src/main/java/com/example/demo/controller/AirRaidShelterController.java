package com.example.demo.controller;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.service.AirRaidShelterService;
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
public class AirRaidShelterController {

    private final AirRaidShelterService airRaidShelterService;

    // /shelter 또는 /shelters/air 둘 다 이 메서드로 들어오도록 통합
    @GetMapping({"/shelter", "/shelters/air"})
    public String airShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(size = 10) Pageable pageable,
            Model model
    ) {
        Page<AirRaidShelter> shelters = airRaidShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "shelter"; // shelter.html 템플릿 반환
    }

    // 우측 상단 [공습 대피소 DB 연동] 버튼 클릭 시 실행되는 매핑
    @PostMapping("/shelters/sync/air")
    public String syncAirShelters() {
        airRaidShelterService.syncData();
        return "redirect:/shelters/air";
    }



    // 모달창에서 대피소 등록을 처리하는 POST 매핑 (saveShelter -> save 로 수정)
    @PostMapping("/shelter/write")
    public String writeAirShelter(AirRaidShelter shelter) {
        airRaidShelterService.save(shelter); // 👈 saveShelter를 save로 변경
        return "redirect:/shelters/air";
    }
}