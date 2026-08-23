package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

// 👉 [⚠️ 팀원과 맞출 부분: 테이블명 admin_account]
@Entity
@Table(name = "admin_account")
@Getter
@Setter
@NoArgsConstructor
public class AdminAccount {

    // 👉 [⚠️ 팀원과 맞출 부분: 기본키 admin_id]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    // 👉 [⚠️ 팀원과 맞출 부분: 사원명 emp_name]
    @Column(name = "emp_name", nullable = false, length = 50)
    private String empName;

    // 👉 [⚠️ 팀원과 맞출 부분: 사원번호 emp_no]
    @Column(name = "emp_no", nullable = false, unique = true, length = 50)
    private String empNo;

    // 👉 [⚠️ 팀원과 맞출 부분: 로그인 ID username]
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    // 👉 [⚠️ 팀원과 맞출 부분: 비밀번호 password]
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // 👉 [⚠️ 팀원과 맞출 부분: 권한 role (ROLE_ADMIN, ROLE_STAFF)]
    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}