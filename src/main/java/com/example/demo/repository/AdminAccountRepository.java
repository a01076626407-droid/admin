package com.example.demo.repository;

import com.example.demo.domain.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminAccountRepository extends JpaRepository<AdminAccount, Long> {

    // 👉 [⚠️ 팀원이 로그인/조회 시 호출할 핵심 메서드명: findByUsername]
    Optional<AdminAccount> findByUsername(String username);

    // 👉 [⚠️ 아이디 중복 체크용: existsByUsername]
    boolean existsByUsername(String username);

    // 👉 [⚠️ 사원번호 중복 체크용: existsByEmpNo]
    boolean existsByEmpNo(String empNo);
}
