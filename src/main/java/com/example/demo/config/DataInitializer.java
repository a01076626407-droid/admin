package com.example.demo.config;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("super")) {
            User superAdmin = User.builder()
                    .username("super")
                    .password("super")
                    .email("super@admin.com")
                    .realname("최고관리자")
                    .role("SUPER")
                    .canEdit(true)
                    .canDelete(true)
                    .build();
            userRepository.save(superAdmin);
            System.out.println(">>> [스프링 초기화] 최고 관리자(super) 계정이 생성되었습니다. (PW: super)");
        }
    }
}