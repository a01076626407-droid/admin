package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 로그인 검증
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    // 회원가입 처리
    @Transactional
    public void registerUser(UserSignupDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .email(dto.getEmail())
                .realname(dto.getRealname())
                .role("USER")
                .canEdit(false)
                .canDelete(false)
                .build();
        userRepository.save(user);
    }

    // 💡 전체 회원 목록 조회 (권한 관리 페이지용)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 💡 개별 유저 수정/삭제 권한 업데이트 로직 (ADMIN 자동 승격/강등 포함)
    @Transactional
    public void updateUserPermissions(Long userId, boolean canEdit, boolean canDelete) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        user.setCanEdit(canEdit);
        user.setCanDelete(canDelete);

        // 수정과 삭제 권한을 모두 받으면 ADMIN으로 승격, 아니면 일반 유저 상태 유지 (SUPER 제외)
        if (canEdit && canDelete) {
            if (!"SUPER".equals(user.getRole())) {
                user.setRole("ADMIN");
            }
        } else if (!"SUPER".equals(user.getRole())) {
            user.setRole("USER");
        }
    }

    // 최고 관리자 권한 부여 메서드
    @Transactional
    public void grantAdminRole(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + username));
        user.setRole("SUPER");
        user.setCanEdit(true);
        user.setCanDelete(true);
    }
}