package com.example.demo.controller;

import com.example.demo.domain.Post;
import com.example.demo.domain.User;
import com.example.demo.service.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 목록 조회 (페이징 적용)
    @GetMapping("/posts")
    public String getPosts(
            @PageableDefault(page = 0, size = 10) Pageable pageable,
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

    // 게시글 등록 처리
    @PostMapping("/posts/write")
    public String writePost(Post post, HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj != null) {
            if (loginUserObj instanceof User) {
                post.setWriter(((User) loginUserObj).getUsername());
            } else {
                post.setWriter(loginUserObj.toString());
            }
        }

        if (post.getCreateDate() == null) {
            post.setCreateDate(LocalDateTime.now());
        }
        postService.save(post);
        return "redirect:/posts";
    }

    // 게시글 상세 조회
    @GetMapping("/posts/{id}")
    public String getPostDetail(@PathVariable("id") Long id, Model model) {
        Post post = postService.getPostById(id);
        model.addAttribute("post", post);
        return "detail";
    }

    // 게시글 수정 페이지 이동
    @GetMapping("/posts/edit/{id}")
    public String editPostForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();

        boolean isSuper = "super".equals(username);
        boolean isWriter = post.getWriter().equals(username);

        if (!isSuper && !isWriter) {
            return "redirect:/posts";
        }

        model.addAttribute("post", post);
        return "edit";
    }

    // 게시글 수정 처리
    @PostMapping("/posts/edit/{id}")
    public String editPost(@PathVariable("id") Long id, Post postDto, HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();

        boolean isSuper = "super".equals(username);
        boolean isWriter = post.getWriter().equals(username);

        if (!isSuper && !isWriter) {
            return "redirect:/posts";
        }

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        postService.save(post);

        return "redirect:/posts";
    }

    // 게시글 삭제 처리
    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") Long id, HttpSession session) {
        Object loginUserObj = session.getAttribute("loginUser");
        if (loginUserObj == null) {
            return "redirect:/login";
        }

        Post post = postService.getPostById(id);
        String username = (loginUserObj instanceof User) ? ((User) loginUserObj).getUsername() : loginUserObj.toString();

        boolean isSuper = "super".equals(username);
        boolean isWriter = post.getWriter().equals(username);

        if (isSuper || isWriter) {
            postService.delete(id);
        }

        return "redirect:/posts";
    }
}