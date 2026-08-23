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

    @GetMapping("/posts/delete/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        postRepository.deleteById(id);
        return "redirect:/posts";
    }
}