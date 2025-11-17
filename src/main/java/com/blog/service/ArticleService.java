package com.blog.service;

import com.blog.dto.request.ArticleRequest;
import com.blog.dto.response.ArticleResponse;
import com.blog.entity.Article;
import com.blog.entity.User;
import com.blog.entity.Tag;
import com.blog.exception.BlogException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final AIContentService aiContentService;
    private final SlugService slugService;

    /**
     * 获取文章列表（分页）
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> getArticles(Pageable pageable, String tag) {
        Page<Article> articles;
        System.out.println("=== 开始查询文章 ===");
        System.out.println("查询状态: DRAFT");
        System.out.println("分页参数: page=" + pageable.getPageNumber() + ", size=" + pageable.getPageSize());
        if (tag != null && !tag.trim().isEmpty()) {
            articles = articleRepository.findByTagsNameAndStatus(tag, Article.ArticleStatus.DRAFT, pageable);
        } else {
            articles = articleRepository.findByStatus(Article.ArticleStatus.DRAFT, pageable);
        }
        System.out.println("查询结果数量: " + articles.getNumberOfElements());
        System.out.println("总文章数: " + articles.getTotalElements());

        return articles.map(article ->
                ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }

    /**
     * 根据ID获取文章
     */
    @Transactional(readOnly = true)
    public ArticleResponse getArticleById(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        // 增加阅读计数
        article.incrementViewCount();
        articleRepository.save(article);

        // 检查当前用户是否点赞（这里需要从安全上下文获取当前用户）
        boolean liked = false; // 默认值，实际应该从安全上下文获取

        return ArticleResponse.fromArticleWithAuthorAndLikeStatus(article, article.getAuthor(), liked);
    }

    /**
     * 根据slug获取文章
     */
    @Transactional(readOnly = true)
    public ArticleResponse getArticleBySlug(String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        // 增加阅读计数
        article.incrementViewCount();
        articleRepository.save(article);

        boolean liked = false;
        return ArticleResponse.fromArticleWithAuthorAndLikeStatus(article, article.getAuthor(), liked);
    }

    /**
     * 创建文章
     */
    public ArticleResponse createArticle(ArticleRequest request, User author) {
        // 生成唯一的slug
        String slug = slugService.generateSlug(request.getTitle());

        // 创建文章
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());
        article.setSlug(slug);
        article.setAuthor(author);

        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = Tag.create(tagName);
                                return tagRepository.save(newTag);
                            }))
                    .collect(Collectors.toSet());
            article.setTags(tags);
        }

        // 如果请求发布，则发布文章
        if (Boolean.TRUE.equals(request.getPublish())) {
            article.publish();
        }

        Article savedArticle = articleRepository.save(article);

        // 使用AI生成内容
        if (Boolean.TRUE.equals(request.getGenerateAISummary()) ||
                Boolean.TRUE.equals(request.getGenerateAITags())) {
            generateAIContent(savedArticle);
        }

        return ArticleResponse.fromArticleWithAuthor(savedArticle, author);
    }

    /**
     * 更新文章
     */
    public ArticleResponse updateArticle(Long articleId, ArticleRequest request, User user) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        // 检查权限
        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new BlogException("没有权限修改此文章", HttpStatus.FORBIDDEN);
        }

        // 更新文章内容
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setSummary(request.getSummary());

        // 更新标签
        if (request.getTags() != null) {
            Set<Tag> tags = request.getTags().stream()
                    .map(tagName -> tagRepository.findByName(tagName)
                            .orElseGet(() -> {
                                Tag newTag = Tag.create(tagName);
                                return tagRepository.save(newTag);
                            }))
                    .collect(Collectors.toSet());
            article.setTags(tags);
        }

        Article savedArticle = articleRepository.save(article);

        return ArticleResponse.fromArticleWithAuthor(savedArticle, user);
    }

    /**
     * 删除文章
     */
    public void deleteArticle(Long articleId, User user) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        // 检查权限
        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new BlogException("没有权限删除此文章", HttpStatus.FORBIDDEN);
        }

        articleRepository.delete(article);
    }

    /**
     * 发布文章
     */
    public ArticleResponse publishArticle(Long articleId, User user) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        // 检查权限
        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new BlogException("没有权限发布此文章", HttpStatus.FORBIDDEN);
        }

        article.publish();
        Article savedArticle = articleRepository.save(article);

        return ArticleResponse.fromArticleWithAuthor(savedArticle, user);
    }

    /**
     * 取消发布文章
     */
    public ArticleResponse unpublishArticle(Long articleId, User user) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        // 检查权限
        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new BlogException("没有权限取消发布此文章", HttpStatus.FORBIDDEN);
        }

        article.unpublish();
        Article savedArticle = articleRepository.save(article);

        return ArticleResponse.fromArticleWithAuthor(savedArticle, user);
    }

    /**
     * 搜索文章
     */
    @Transactional(readOnly = true)
    public Page<ArticleResponse> searchArticles(String keyword, Pageable pageable) {
        Page<Article> articles = articleRepository.searchArticles(keyword, pageable);
        return articles.map(article ->
                ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()));
    }

    /**
     * 获取热门文章
     */
    @Transactional(readOnly = true)
    public List<ArticleResponse> getPopularArticles(int limit) {
        List<Article> articles = articleRepository.findPopularArticles(limit);
        return articles.stream()
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()))
                .collect(Collectors.toList());
    }

    /**
     * 生成AI内容
     */
    private void generateAIContent(Article article) {
        try {
            // 生成AI摘要
            String aiSummary = aiContentService.generateArticleSummary(article.getContent());
            if (aiSummary != null && !aiSummary.trim().isEmpty()) {
                article.setAiSummary(aiSummary);
            }

            // 生成AI标签
            Set<String> aiTags = aiContentService.generateTags(article.getContent());
            if (aiTags != null && !aiTags.isEmpty()) {
                article.setAiTags(aiTags);
            }

            articleRepository.save(article);
        } catch (Exception e) {
            // AI服务可能不可用，记录错误但不中断流程
            // 在实际应用中应该使用日志记录
            System.err.println("AI内容生成失败: " + e.getMessage());
        }
    }
}