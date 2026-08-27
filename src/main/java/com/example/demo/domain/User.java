package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String realname;

    @Builder.Default
    @Column(nullable = false)
    private String role = "USER";

    // 💡 개별 수정 권한 (기본값: false)
    @Builder.Default
    @Column(nullable = false)
    private boolean canEdit = false;

    // 💡 개별 삭제 권한 (기본값: false)
    @Builder.Default
    @Column(nullable = false)
    private boolean canDelete = false;
}