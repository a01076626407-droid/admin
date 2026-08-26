package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class PythonScriptRunner {

    @PostConstruct
    public void runPythonScript() {
        try {
            // 프로젝트 루트 경로 기준 scripts 폴더의 파이썬 파일 실행 설정
            ProcessBuilder pb = new ProcessBuilder("python", "scripts/excel_to_mysql.py");

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 파이썬 실행 결과 로그 출력 확인용
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python Log] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println(">>> 파이썬 스크립트 실행 종료 (종료 코드: " + exitCode + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}