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
        // 💡 윈도우와 리눅스 환경에 맞는 파이썬 실행 경로 지정
        // 리눅스 서버에서 가상환경(venv 등)을 쓴다면 아래 경로를 가상환경 내 python 경로로 수정 가능합니다.
        String os = System.getProperty("os.name").toLowerCase();
        String pythonCmd;

        if (os.contains("win")) {
            pythonCmd = "python"; // 윈도우 환경
        } else {
            // 리눅스 서버: 시스템 python3 또는 가상환경 python 경로 지정 (예: "/home/admin/venv/bin/python3" 등)
            pythonCmd = "python3";
        }

        String[] scripts = {
                "scripts/excel_to_mysql.py",
                "scripts/airstrike.py",
                "scripts/earthquake.py"
        };

        for (String scriptPath : scripts) {
            runSingleScript(pythonCmd, scriptPath, os);
        }
    }

    private void runSingleScript(String pythonCmd, String scriptPath, String os) {
        try {
            System.out.println("\n>>> [PythonRunner] 실행 시작: " + scriptPath + " (사용 명령어: " + pythonCmd + ")");

            ProcessBuilder pb = new ProcessBuilder(pythonCmd, scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 인코딩 처리 (윈도우 MS949/EUC-KR, 리눅스 UTF-8)
            String charset = os.contains("win") ? "MS949" : StandardCharsets.UTF_8.name();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python Log][" + scriptPath + "] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println(">>> [PythonRunner] 실행 종료 (" + scriptPath + ", 종료 코드: " + exitCode + ")");

            if (exitCode != 0) {
                System.out.println("[ERROR] 파이썬 스크립트가 비정상 종료되었습니다. (코드: " + exitCode + ")");
            }

        } catch (Exception e) {
            System.out.println("[WARN] 파이썬 스크립트 실행 실패 (" + scriptPath + "): " + e.getMessage());
        }
    }
}