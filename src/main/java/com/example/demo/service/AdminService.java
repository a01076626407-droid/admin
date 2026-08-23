package com.example.demo.service;

import com.example.demo.domain.AdminAccount;
import com.example.demo.repository.AdminAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminAccountRepository adminAccountRepository;

    // 1. 신규 관리자 등록 로직 (중복 방어 포함)
    @Transactional
    public AdminAccount registerAdmin(String empName, String empNo, String username, String password) {
        if (adminAccountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        if (adminAccountRepository.existsByEmpNo(empNo)) {
            throw new IllegalArgumentException("이미 존재하는 사원번호입니다.");
        }

        AdminAccount account = new AdminAccount();
        account.setEmpName(empName);
        account.setEmpNo(empNo);
        account.setUsername(username);
        account.setPassword(password);
        account.setRole("ROLE_STAFF"); // 새로 생성된 계정은 일반 관리자로 설정

        return adminAccountRepository.save(account);
    }

    // 2. 로그인 인증 로직 (아이디 및 비밀번호 검증)
    @Transactional(readOnly = true)
    public Optional<AdminAccount> authenticate(String username, String password) {
        return adminAccountRepository.findByUsername(username)
                .filter(admin -> admin.getPassword().equals(password));
    }

    // 3. 전체 관리자 목록 조회 로직
    @Transactional(readOnly = true)
    public List<AdminAccount> getAllAdmins() {
        return adminAccountRepository.findAll();
    }
}