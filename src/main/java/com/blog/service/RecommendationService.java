package com.blog.service;

import com.blog.dto.response.ArticleResponse;
import com.blog.entity.Article;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final ArticleRepository articleRepository;
    private final LikeRepository likeRepository;
    private final AIContentService aiContentService;

    /**
     * 基于内容的文章推荐
     */
    public List<ArticleResponse> getContentBasedRecommendations(Long articleId, int limit) {
        Article targetArticle = articleRepository.findById(articleId).orElse(null);
        if (targetArticle == null) {
            return getPopularArticles(limit);
        }

        // 获取目标文章的标签
        Set<String> targetTags = targetArticle.getTagNames();
        if (targetTags.isEmpty()) {
            return getPopularArticles(limit);
        }

        // 查找具有相似标签的文章
        List<Article> similarArticles = articleRepository.findByTagsInAndIdNot(
                targetTags, articleId, PageRequest.of(0, limit * 2));

        // 按标签匹配度排序
        similarArticles.sort((a1, a2) -> {
            double score1 = calculateTagSimilarity(targetTags, a1.getTagNames());
            double score2 = calculateTagSimilarity(targetTags, a2.getTagNames());
            return Double.compare(score2, score1); // 降序排序
        });

        // 限制返回数量
        return similarArticles.stream()
                .limit(limit)
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()))
                .collect(Collectors.toList());
    }

    /**
     * 基于用户行为的推荐
     */
    public List<ArticleResponse> getUserBasedRecommendations(Long userId, int limit) {
        // 获取用户点赞的文章
        List<Article> userLikedArticles = likeRepository.findLikedArticlesByUserId(userId);

        if (userLikedArticles.isEmpty()) {
            return getPopularArticles(limit);
        }

        // 基于用户点赞的文章标签进行推荐
        Set<String> userInterests = userLikedArticles.stream()
                .flatMap(article -> article.getTagNames().stream())
                .collect(Collectors.toSet());

        // 查找具有用户感兴趣标签的文章
        List<Article> recommendedArticles = articleRepository.findByTagsInAndUserIdNot(
                userInterests, userId, PageRequest.of(0, limit * 2));

        // 按用户兴趣匹配度排序
        recommendedArticles.sort((a1, a2) -> {
            double score1 = calculateTagSimilarity(userInterests, a1.getTagNames());
            double score2 = calculateTagSimilarity(userInterests, a2.getTagNames());
            return Double.compare(score2, score1);
        });

        return recommendedArticles.stream()
                .limit(limit)
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()))
                .collect(Collectors.toList());
    }

    /**
     * 热门文章推荐
     */
    public List<ArticleResponse> getPopularArticles(int limit) {
        List<Article> popularArticles = articleRepository.findPopularArticles(limit);
        return popularArticles.stream()
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()))
                .collect(Collectors.toList());
    }

    /**
     * 最新文章推荐
     */
    public List<ArticleResponse> getRecentArticles(int limit) {
        List<Article> recentArticles = articleRepository.findRecentArticles(limit);
        return recentArticles.stream()
                .map(article -> ArticleResponse.fromArticleWithAuthor(article, article.getAuthor()))
                .collect(Collectors.toList());
    }

    /**
     * 混合推荐（结合多种推荐策略）
     */
    public List<ArticleResponse> getHybridRecommendations(User user, int limit) {
        List<ArticleResponse> recommendations = new ArrayList<>();

        // 基于用户行为的推荐（如果用户有行为数据）
        if (user != null) {
            List<ArticleResponse> userBased = getUserBasedRecommendations(user.getId(), limit / 2);
            recommendations.addAll(userBased);
        }

        // 如果推荐数量不足，补充热门文章
        if (recommendations.size() < limit) {
            int remaining = limit - recommendations.size();
            List<ArticleResponse> popular = getPopularArticles(remaining);

            // 去重
            Set<Long> existingIds = recommendations.stream()
                    .map(ArticleResponse::getId)
                    .collect(Collectors.toSet());

            popular.stream()
                    .filter(article -> !existingIds.contains(article.getId()))
                    .forEach(recommendations::add);
        }

        // 如果还是不足，补充最新文章
        if (recommendations.size() < limit) {
            int remaining = limit - recommendations.size();
            List<ArticleResponse> recent = getRecentArticles(remaining);

            Set<Long> existingIds = recommendations.stream()
                    .map(ArticleResponse::getId)
                    .collect(Collectors.toSet());

            recent.stream()
                    .filter(article -> !existingIds.contains(article.getId()))
                    .forEach(recommendations::add);
        }

        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 计算标签相似度
     */
    private double calculateTagSimilarity(Set<String> tags1, Set<String> tags2) {
        if (tags1.isEmpty() || tags2.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(tags1);
        intersection.retainAll(tags2);

        Set<String> union = new HashSet<>(tags1);
        union.addAll(tags2);

        return (double) intersection.size() / union.size();
    }

    /**
     * 获取相关文章（用于文章详情页）
     */
    public List<ArticleResponse> getRelatedArticles(Long articleId, int limit) {
        return getContentBasedRecommendations(articleId, limit);
    }

    /**
     * 基于AI的内容相似度推荐（高级功能）
     */
    public List<ArticleResponse> getAIBasedRecommendations(Long articleId, int limit) {
        // 注意：这是一个高级功能，需要AI服务支持语义相似度计算
        // 这里只是一个框架实现

        Article targetArticle = articleRepository.findById(articleId).orElse(null);
        if (targetArticle == null || !aiContentService.isAIServiceAvailable()) {
            return getContentBasedRecommendations(articleId, limit);
        }

        // 在实际实现中，这里会使用AI计算文章之间的语义相似度
        // 由于实现复杂度较高，这里回退到基于内容的推荐
        log.info("AI推荐功能暂未实现，使用基于内容的推荐");
        return getContentBasedRecommendations(articleId, limit);
    }
}