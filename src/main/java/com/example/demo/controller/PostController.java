package com.example.demo.controller;

import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. 최신글 맨 상단 배치 (id 내림차순 정렬)
    @GetMapping("/posts")
    public String getPosts(
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<Post> posts = postService.getPosts(pageable);
        model.addAttribute("posts", posts);
        return "list";
    }

    // 게시글 작성 페이지 이동
    @GetMapping("/posts/write")
    public String writePostForm(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        return "write";
    }

    // 게시글 등록 처리 (공백/스페이스바 방어)
    @PostMapping("/posts/write")
    public String writePost(Post post, HttpSession session, RedirectAttributes redirectAttributes) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        // 백엔드 제목/내용 공백(스페이스바) 유효성 검사
        if (post.getTitle() == null || post.getTitle().trim().isEmpty() ||
                post.getContent() == null || post.getContent().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "제목과 내용은 공백 없이 1자 이상 입력해야 합니다.");
            return "redirect:/posts/write";
        }

        if (loginUserObj instanceof User) {
            post.setWriter(((User) loginUserObj).getUsername());
        } else {
            post.setWriter(loginUserObj.toString());
        }

        if (post.getCreateDate() == null) {
            post.setCreateDate(LocalDateTime.now());
        }
        postService.save(post);
        return "redirect:/posts";
    }

    // 2. 게시글 상세 조회 (존재하지 않는 글 예외 처리 + 권한 판별)
    @GetMapping("/posts/{id}")
    public String getPostDetail(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Post post;
        try {
            post = postService.getPostById(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "redirect:/posts";
        }

        model.addAttribute("post", post);

        Object loginUserObj = session.getAttribute("loginUser");
        Boolean sessionCanEdit = (Boolean) session.getAttribute("canEdit");
        Boolean sessionCanDelete = (Boolean) session.getAttribute("canDelete");

        boolean canEdit = false;
        boolean canDelete = false;

        if (loginUserObj != null) {
            String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();
            String loginRole = (String) session.getAttribute("loginRole");
            boolean isSuper = "SUPER".equals(loginRole) || "super".equals(username);
            boolean isWriter = post.getWriter().equals(username);

            // 수정 권한: 작성자 본인 OR 최고관리자 OR 개별 수정 승인자
            if (isSuper || isWriter || Boolean.TRUE.equals(sessionCanEdit)) {
                canEdit = true;
            }
            // 삭제 권한: 작성자 본인 OR 최고관리자 OR 개별 삭제 승인자
            if (isSuper || isWriter || Boolean.TRUE.equals(sessionCanDelete)) {
                canDelete = true;
            }
        }

        model.addAttribute("canEdit", canEdit);
        model.addAttribute("canDelete", canDelete);
        return "detail";
    }

    // 💡 3. 게시글 수정 페이지 이동 (존재 여부 + URL 권한 방어)
    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable("id") Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        Post post;
        try {
            post = postService.getPostById(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "redirect:/posts";
        }

        String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();
        String loginRole = (String) session.getAttribute("loginRole");
        Boolean sessionCanEdit = (Boolean) session.getAttribute("canEdit");

        boolean isSuper = "SUPER".equals(loginRole) || "super".equals(username);
        boolean isWriter = post.getWriter().equals(username);
        boolean hasEditAuth = Boolean.TRUE.equals(sessionCanEdit);

        // 작성자 본인도 아니고, 최고관리자도 아니고, 수정 권한도 없으면 차단
        if (!isSuper && !isWriter && !hasEditAuth) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 게시글의 수정 권한이 없습니다.");
            return "redirect:/posts";
        }

        model.addAttribute("post", post);
        return "edit";
    }

    // 💡 4. 게시글 수정 처리 (존재 여부 + URL 권한 방어 + 공백 방어)
    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Post postDto, HttpSession session, RedirectAttributes redirectAttributes) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        Post post;
        try {
            post = postService.getPostById(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "redirect:/posts";
        }

        String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();
        String loginRole = (String) session.getAttribute("loginRole");
        Boolean sessionCanEdit = (Boolean) session.getAttribute("canEdit");

        boolean isSuper = "SUPER".equals(loginRole) || "super".equals(username);
        boolean isWriter = post.getWriter().equals(username);
        boolean hasEditAuth = Boolean.TRUE.equals(sessionCanEdit);

        if (!isSuper && !isWriter && !hasEditAuth) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 게시글의 수정 권한이 없습니다.");
            return "redirect:/posts";
        }

        // 공백(스페이스바) 유효성 검사
        if (postDto.getTitle() == null || postDto.getTitle().trim().isEmpty() ||
                postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "제목과 내용은 공백 없이 1자 이상 입력해야 합니다.");
            return "redirect:/posts/edit/" + id;
        }

        // 작성자는 유지하고 제목과 내용만 수정
        post.setTitle(postDto.getTitle().trim());
        post.setContent(postDto.getContent().trim());
        postService.save(post);

        return "redirect:/posts";
    }

    // 💡 5. 게시글 삭제 처리 (존재 여부 + URL 권한 방어)
    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        Post post;
        try {
            post = postService.getPostById(id);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 게시글입니다.");
            return "redirect:/posts";
        }

        String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();
        String loginRole = (String) session.getAttribute("loginRole");
        Boolean sessionCanDelete = (Boolean) session.getAttribute("canDelete");

        boolean isSuper = "SUPER".equals(loginRole) || "super".equals(username);
        boolean isWriter = post.getWriter().equals(username);
        boolean hasDeleteAuth = Boolean.TRUE.equals(sessionCanDelete);

        if (!isSuper && !isWriter && !hasDeleteAuth) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 게시글의 삭제 권한이 없습니다.");
            return "redirect:/posts";
        }

        postService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
        return "redirect:/posts";
    }
}