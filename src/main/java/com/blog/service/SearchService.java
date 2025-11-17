package com.blog.service;

import com.blog.dto.response.ArticleResponse;
import com.blog.entity.Article;
import com.blog.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final ArticleRepository articleRepository;

    /**
     * 搜索文章
     */
    public Page<ArticleResponse> searchArticles(String query, Pageable pageable) {
        Page<Article> articles = articleRepository.searchArticles(query, pageable);
        return articles.map(article ->
                ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }

    /**
     * 高级搜索（多字段搜索）
     */
    public Page<ArticleResponse> advancedSearch(String keyword, String author, List<String> tags,
                                                Pageable pageable) {
        Page<Article> articles = articleRepository.advancedSearch(keyword, author, pageable);
        return articles.map(article ->
                ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }

    /**
     * 获取搜索建议
     */
    public List<String> getSearchSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        // 基于文章标题的搜索建议
        List<Article> articles = articleRepository.findByTitleContaining(query,
                org.springframework.data.domain.PageRequest.of(0, 5));

        return articles.stream()
                .map(Article::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门搜索关键词
     */
    public List<String> getPopularSearchKeywords(int limit) {
        // 在实际应用中，这里应该从搜索日志中统计热门关键词
        // 这里返回一些默认的关键词
        return List.of("Spring Boot", "Java", "编程", "技术", "学习", "开发", "教程");
    }

    /**
     * 标签搜索
     */
    public Page<ArticleResponse> searchByTag(String tag, Pageable pageable) {
        Page<Article> articles = articleRepository.findByTagsNameAndStatus(
                tag, Article.ArticleStatus.PUBLISHED, pageable);
        return articles.map(article ->
                ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }

    /**
     * 作者搜索
     */
    public Page<ArticleResponse> searchByAuthor(String authorName, Pageable pageable) {
        Page<Article> articles = articleRepository.findByAuthorUsernameContainingAndStatus(
                authorName, Article.ArticleStatus.PUBLISHED, pageable);
        return articles.map(article ->
                ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }
}