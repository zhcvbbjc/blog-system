package com.blog.service;

import com.blog.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SlugService {

    private final ArticleRepository articleRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    /**
     * 生成唯一的slug
     */
    public String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return UUID.randomUUID().toString().replace("-", "");
        }

        String base = createSlug(title);

        // 如果中文导致 slug 为空
        if (base.isBlank()) {
            base = "article";
        }

        String slug = base;
        int counter = 1;

        while (articleRepository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }

        return slug;
    }

    /**
     * 创建基础的slug
     */
    private String createSlug(String input) {
        if (input == null) {
            throw new IllegalArgumentException("输入不能为空");
        }

        // 转换为小写，移除重音符号
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");

        // 移除连续的连字符
        slug = slug.replaceAll("-{2,}", "-");

        // 移除开头和结尾的连字符
        slug = slug.replaceAll("^-|-$", "");

        return slug.toLowerCase();
    }
}