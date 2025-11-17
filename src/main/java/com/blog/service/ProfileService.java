package com.blog.service;

import com.blog.dto.response.ProfileResponse;
import com.blog.dto.response.UserActivityResponse;
import com.blog.dto.response.UserResponse;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.Like;
import com.blog.entity.User;
import com.blog.exception.BlogException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.LikeRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    /**
     * 获取个人主页信息
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        // 获取用户信息
        UserResponse userResponse = UserResponse.fromUser(user);

        // 获取统计数据
        ProfileResponse.UserStats stats = getUserStats(userId);

        // 获取最近的文章（最多5篇）
        List<Article> recentArticles = articleRepository.findByAuthor(user, 
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
        List<ProfileResponse.ArticlePreview> articlePreviews = recentArticles.stream()
                .map(this::toArticlePreview)
                .collect(Collectors.toList());

        // 获取最近的活动（最多5条）
        List<UserActivityResponse> recentActivities = getRecentActivities(userId, 5);

        // 转换为 ProfileResponse.UserActivity
        List<ProfileResponse.UserActivity> userActivities = recentActivities.stream()
                .map(activity -> new ProfileResponse.UserActivity(
                        activity.getType().name(),
                        activity.getDescription(),
                        activity.getTargetId(),
                        activity.getTargetTitle(),
                        activity.getActivityTime()
                ))
                .collect(Collectors.toList());

        // 构建响应
        ProfileResponse response = new ProfileResponse();
        response.setUser(userResponse);
        response.setStats(stats);
        response.setRecentArticles(articlePreviews);
        response.setRecentActivities(userActivities);

        return response;
    }

    /**
     * 获取用户统计数据
     */
    @Transactional(readOnly = true)
    public ProfileResponse.UserStats getUserStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        // 获取用户的所有文章
        List<Article> userArticles = articleRepository.findByAuthor(user, Pageable.unpaged()).getContent();

        // 统计文章数量
        int totalArticles = userArticles.size();

        // 统计点赞数（用户文章收到的总点赞数）
        int totalLikes = userArticles.stream()
                .mapToInt(article -> likeRepository.countByArticle(article))
                .sum();

        // 统计评论数（用户文章收到的总评论数）
        int totalComments = userArticles.stream()
                .mapToInt(article -> commentRepository.countByArticle(article))
                .sum();

        // 统计浏览量（用户文章的总浏览量）
        int totalViews = userArticles.stream()
                .mapToInt(Article::getViewCount)
                .sum();

        // 关注数和粉丝数（如果系统中没有实现关注功能，暂时设为0）
        int followers = 0;
        int following = 0;

        return new ProfileResponse.UserStats(
                totalArticles, totalLikes, totalComments, totalViews, followers, following
        );
    }

    /**
     * 获取用户活动时间线
     */
    @Transactional(readOnly = true)
    public Page<UserActivityResponse> getUserTimeline(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        // 获取所有活动
        List<UserActivityResponse> allActivities = getAllUserActivities(user);

        // 计算总数
        int total = allActivities.size();
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, total);

        // 分页
        List<UserActivityResponse> pagedActivities = start < total 
                ? allActivities.subList(start, end) 
                : new ArrayList<>();

        // 创建分页对象
        return new PageImpl<>(pagedActivities, pageable, total);
    }

    /**
     * 获取用户最近的活动
     */
    @Transactional(readOnly = true)
    public List<UserActivityResponse> getRecentActivities(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        // 获取所有活动并按时间排序
        List<UserActivityResponse> allActivities = getAllUserActivities(user);

        // 限制数量
        return allActivities.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的所有活动
     */
    private List<UserActivityResponse> getAllUserActivities(User user) {
        List<UserActivityResponse> activities = new ArrayList<>();

        // 1. 获取用户创建的文章活动
        List<Article> articles = articleRepository.findByAuthor(user, Pageable.unpaged()).getContent();
        for (Article article : articles) {
            activities.add(UserActivityResponse.articleCreated(
                    article.getId(), 
                    article.getTitle(), 
                    article.getCreatedAt()
            ));
        }

        // 2. 获取用户发表的评论活动
        List<Comment> comments = commentRepository.findByUserOrderByCreatedAtDesc(user);
        for (Comment comment : comments) {
            activities.add(UserActivityResponse.commentCreated(
                    comment.getArticle().getId(),
                    comment.getArticle().getTitle(),
                    comment.getCreatedAt()
            ));
        }

        // 3. 获取用户的点赞活动
        List<Like> likes = likeRepository.findByUser(user);
        for (Like like : likes) {
            if (like.isLike()) {
                activities.add(UserActivityResponse.likeAdded(
                        like.getArticle().getId(),
                        like.getArticle().getTitle(),
                        like.getCreatedAt()
                ));
            }
        }

        // 按时间倒序排序
        activities.sort(Comparator.comparing(UserActivityResponse::getActivityTime).reversed());

        return activities;
    }

    /**
     * 将文章转换为预览信息
     */
    private ProfileResponse.ArticlePreview toArticlePreview(Article article) {
        ProfileResponse.ArticlePreview preview = new ProfileResponse.ArticlePreview();
        preview.setId(article.getId());
        preview.setTitle(article.getTitle());
        preview.setSummary(article.getSummary());
        preview.setSlug(article.getSlug());
        preview.setViewCount(article.getViewCount());
        preview.setLikeCount(article.getLikeCount());
        preview.setCommentCount(article.getCommentCount());
        preview.setCreatedAt(article.getCreatedAt());
        return preview;
    }
}

