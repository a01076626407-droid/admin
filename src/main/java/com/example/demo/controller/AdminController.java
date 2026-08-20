package com.example.demo.controller;

import com.example.demo.domain.AdminAccount;
import com.example.demo.repository.AdminAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminController implements CommandLineRunner {

    private final AdminAccountRepository adminAccountRepository;

    @Override
    public void run(String... args) throws Exception {
        // 서버 실행 시 DB에 admin 계정이 없으면 자동으로 1건 생성
        if (!adminAccountRepository.existsByUsername("admin")) {
            AdminAccount rootAdmin = new AdminAccount();
            rootAdmin.setEmpName("최고관리자");
            rootAdmin.setEmpNo("ROOT-001");
            rootAdmin.setUsername("admin");
            rootAdmin.setPassword("admin");
            rootAdmin.setRole("ROLE_ADMIN"); // 최고 관리자 권한 부여

            adminAccountRepository.save(rootAdmin);
            System.out.println("=================================================");
            System.out.println("🎉 [초기화 완료] 기본 관리자 계정 생성 (ID: admin / PW: admin)");
            System.out.println("=================================================");
        }
    }
}
