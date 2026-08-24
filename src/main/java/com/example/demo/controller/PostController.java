package com.example.demo.controller;

import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping("/posts")
    public String getPostsPage(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Post> paging = postService.getPosts(page);
        model.addAttribute("paging", paging);
        return "list";
    }

    @GetMapping("/posts/write")
    public String writePage() {
        return "write";
    }

    @PostMapping("/posts/write")
    public String writePost(Post post, jakarta.servlet.http.HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            loginUser = "kyohun00";
        }
        post.setWriter(loginUser);

        // 글이 작성되는 순간의 현재 시간을 객체에 주입합니다!
        post.setCreateDate(LocalDateTime.now());

        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String detailPage(@PathVariable("id") Long id, Model model) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/posts";
        }
        model.addAttribute("post", post);
        return "detail";
    }

    // 🟢 수정 페이지 이동 (super 계정 또는 작성자 본인만 허용)
    @GetMapping("/posts/edit/{id}")
    public String editPage(@PathVariable("id") Long id, Model model, jakarta.servlet.http.HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/posts";
        }

        if ("super".equals(loginUser) || loginUser.equals(post.getWriter())) {
            model.addAttribute("post", post);
            return "edit";
        }

        return "redirect:/posts";
    }

    // 🟢 수정 내용 저장 처리 (super 계정 또는 작성자 본인만 허용)
    @PostMapping("/posts/edit/{id}")
    public String updatePost(@PathVariable("id") Long id, Post updatedPost, jakarta.servlet.http.HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            if ("super".equals(loginUser) || loginUser.equals(post.getWriter())) {
                post.setTitle(updatedPost.getTitle());
                post.setContent(updatedPost.getContent());
                postRepository.save(post);
            }
        }

        return "redirect:/posts/{id}";
    }

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") Long id, jakarta.servlet.http.HttpSession session) {
        String loginUser = (String) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Post post = postRepository.findById(id).orElse(null);
        if (post == null) {
            return "redirect:/posts";
        }

        // 'super' 계정이거나, 글 작성자와 현재 로그인한 유저가 정확히 일치할 때만 삭제 허용
        boolean isSuper = "super".equals(loginUser);
        boolean isWriter = loginUser.equals(post.getWriter());

        if (isSuper || isWriter) {
            postRepository.deleteById(id);
        } else {
            // 권한이 없는 경우 삭제하지 않고 목록으로 돌려보냄
            return "redirect:/posts?error=unauthorized";
        }

        return "redirect:/posts";
    }
}