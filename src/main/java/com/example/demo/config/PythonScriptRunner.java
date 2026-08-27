package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class PythonScriptRunner {

    @PostConstruct
    public void runPythonScripts() {
        String[] scripts = {
                "scripts/excel_to_mysql.py",
                "scripts/airstrike.py",
                "scripts/earthquake.py"
        };

        for (String scriptPath : scripts) {
            runSingleScript(scriptPath);
        }
    }

    private void runSingleScript(String scriptPath) {
        try {
            System.out.println("\n>>> [PythonRunner] 실행 시작: " + scriptPath);

            // 💡 OS 환경 자동 감지 (윈도우: python, 리눅스/맥: python3)
            String os = System.getProperty("os.name").toLowerCase();
            String pythonCmd = os.contains("win") ? "python" : "python3";

            ProcessBuilder pb = new ProcessBuilder(pythonCmd, scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 인코딩 처리 (윈도우 MS949/EUC-KR, 기타 UTF-8 대응)
            String charset = os.contains("win") ? "MS949" : StandardCharsets.UTF_8.name();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python Log][" + scriptPath + "] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println(">>> [PythonRunner] 실행 종료 (" + scriptPath + ", 종료 코드: " + exitCode + ")");

        } catch (Exception e) {
            System.out.println("[WARN] 파이썬 스크립트 실행 생략 (" + scriptPath + "): " + e.getMessage());
        }
    }
}