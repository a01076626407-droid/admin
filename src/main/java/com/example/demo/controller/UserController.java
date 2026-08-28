package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 로그인 페이지 이동
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // 글쓰기 페이지 진입 (로그인 검사)
    @GetMapping("/write")
    public String writePage(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        return "write";
    }

    // 로그인 처리
    @PostMapping("/api/users/login")
    public String login(String username, String password, HttpSession session, Model model) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("loginUser", user.getUsername());
            session.setAttribute("loginRole", user.getRole());
            session.setAttribute("canEdit", user.isCanEdit() || "SUPER".equals(user.getRole()));
            session.setAttribute("canDelete", user.isCanDelete() || "SUPER".equals(user.getRole()));

            return "redirect:/shelters/air";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login";
        }
    }

    // 회원가입 처리 (super 아이디 차단 검증)
    @PostMapping("/api/users/signup")
    public String signup(UserSignupDto dto, Model model, RedirectAttributes redirectAttributes) {
        if (dto.getUsername() != null && "super".equalsIgnoreCase(dto.getUsername().trim())) {
            model.addAttribute("errorMessage", "해당 아이디(SUPER)는 사용할 수 없습니다.");
            return "signup";
        }
        try {
            userService.registerUser(dto);
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다! 로그인해 주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // 최고 관리자(SUPER) 전용: 권한 관리 승인 페이지 이동
    @GetMapping("/admin/users")
    public String userPermissionPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String currentRole = (String) session.getAttribute("loginRole");
        String currentUser = (String) session.getAttribute("loginUser");

        if (!"SUPER".equals(currentRole) && !"super".equals(currentUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "최고 관리자만 접근할 수 있는 페이지입니다.");
            return "redirect:/posts";
        }

        model.addAttribute("userList", userService.getAllUsers());
        return "users"; // 정상적인 뷰 템플릿(users.html) 반환
    }

    // 개별 유저 수정/삭제 권한 실시간 체크박스 비동기(AJAX) 저장
    @PostMapping("/admin/users/permissions/{id}")
    @ResponseBody
    public Map<String, Object> updatePermissions(
            @PathVariable("id") Long id,
            @RequestParam(name = "canEdit", defaultValue = "false") boolean canEdit,
            @RequestParam(name = "canDelete", defaultValue = "false") boolean canDelete,
            HttpSession session) {

        String currentRole = (String) session.getAttribute("loginRole");
        String currentUser = (String) session.getAttribute("loginUser");

        if (!"SUPER".equals(currentRole) && !"super".equals(currentUser)) {
            return Map.of("success", false, "message", "최고 관리자만 가능합니다.");
        }

        // 권한 업데이트
        userService.updateUserPermissions(id, canEdit, canDelete);

        // 변경 후 계산된 최신 역할 확인
        String calculatedRole = (canEdit && canDelete) ? "ADMIN" : "USER";

        return Map.of(
                "success", true,
                "role", calculatedRole
        );
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "로그아웃되었습니다.");
        return "redirect:/login";
    }
}