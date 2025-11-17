package com.blog.controller;

import com.blog.entity.User;
import com.blog.service.ArticleService;
import com.blog.service.ProfileService;
import com.blog.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final ArticleService articleService;
    private final ProfileService profileService;

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tag,
            Model model,
            Authentication authentication) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<com.blog.dto.response.ArticleResponse> articles = articleService.getArticles(pageable, tag);
        
        model.addAttribute("articles", articles.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("title", "首页");
        
        if (authentication != null) {
            User user = SecurityUtils.getUserFromAuthentication(authentication);
            model.addAttribute("user", user);
        }
        
        return "index";
    }

    /**
     * 文章详情页
     */
    @GetMapping("/article/{id}")
    public String articleDetail(@PathVariable Long id, Model model, Authentication authentication) {
        com.blog.dto.response.ArticleResponse article = articleService.getArticleById(id);
        
        model.addAttribute("article", article);
        model.addAttribute("title", article.getTitle());
        
        if (authentication != null) {
            User user = SecurityUtils.getUserFromAuthentication(authentication);
            model.addAttribute("user", user);
        }
        
        return "article-detail";
    }

    /**
     * 个人中心页
     */
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User currentUser = SecurityUtils.getUserFromAuthentication(authentication);
        com.blog.dto.response.ProfileResponse profileResponse = profileService.getProfile(currentUser.getId());
        
        model.addAttribute("profile", profileResponse);
        model.addAttribute("user", currentUser);
        model.addAttribute("title", "个人中心");
        
        return "profile";
    }

    /**
     * 用户个人中心页
     */
    @GetMapping("/user/{userId}")
    public String userProfile(@PathVariable Long userId, Model model, Authentication authentication) {
        com.blog.dto.response.ProfileResponse profileResponse = profileService.getProfile(userId);
        
        model.addAttribute("profile", profileResponse);
        model.addAttribute("title", profileResponse.getUser().getUsername() + " - 个人中心");
        
        if (authentication != null) {
            User user = SecurityUtils.getUserFromAuthentication(authentication);
            model.addAttribute("user", user);
        }
        
        return "profile";
    }

    /**
     * 登录页
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "登录");
        return "login";
    }

    /**
     * 注册页
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("title", "注册");
        return "register";
    }

    /**
     * 搜索页
     */
    @GetMapping("/search")
    public String search(@RequestParam String q, 
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model, 
                         Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.blog.dto.response.ArticleResponse> articles = articleService.searchArticles(q, pageable);
        
        model.addAttribute("articles", articles.getContent());
        model.addAttribute("keyword", q);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("title", "搜索结果: " + q);
        
        if (authentication != null) {
            User user = SecurityUtils.getUserFromAuthentication(authentication);
            model.addAttribute("user", user);
        }
        
        return "search";
    }

    /**
     * 标签页
     */
    @GetMapping("/tags")
    public String tags(Model model, Authentication authentication) {
        model.addAttribute("title", "标签");
        
        if (authentication != null) {
            User user = SecurityUtils.getUserFromAuthentication(authentication);
            model.addAttribute("user", user);
        }
        
        return "tags";
    }

    /**
     * 标签文章列表页
     */
    @GetMapping("/tag/{tagName}")
    public String tagArticles(@PathVariable String tagName,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model,
                              Authentication authentication) {
        Pageable pageable = PageRequest.of(page, size);
        Page<com.blog.dto.response.ArticleResponse> articles = articleService.getArticles(pageable, tagName);
        
        model.addAttribute("articles", articles.getContent());
        model.addAttribute("tagName", tagName);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articles.getTotalPages());
        model.addAttribute("title", "标签: " + tagName);
        
        if (authentication != null) {
            User user = SecurityUtils.getUserFromAuthentication(authentication);
            model.addAttribute("user", user);
        }
        
        return "tag-articles";
    }

    /**
     * 后台管理 - 仪表盘
     */
    @GetMapping("/admin")
    public String adminDashboard(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        
        User user = SecurityUtils.getUserFromAuthentication(authentication);
        if (user.getRole() != User.UserRole.ADMIN) {
            return "redirect:/";
        }
        
        model.addAttribute("user", user);
        model.addAttribute("adminPage", true);
        model.addAttribute("title", "后台管理");
        
        return "admin/dashboard";
    }
}

