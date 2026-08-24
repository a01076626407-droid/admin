package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 사용자가 http://localhost:8080/ 에 접속했을 때
    @GetMapping("/")
    public String index() {
        // templates/index.html 파일을 첫 화면으로 띄웁니다.
        return "index";
    }
}