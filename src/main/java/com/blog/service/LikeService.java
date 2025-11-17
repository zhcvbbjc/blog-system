package com.blog.service;

import com.blog.entity.Article;
import com.blog.entity.Like;
import com.blog.entity.User;
import com.blog.exception.BlogException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.LikeRepository;
import com.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    /**
     * 点赞文章
     */
    public void likeArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        // 检查是否已经点赞
        Optional<Like> existingLike = likeRepository.findByUserAndArticle(user, article);
        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            if (like.isLike()) {
                throw new BlogException("您已经点赞过此文章", HttpStatus.CONFLICT);
            } else {
                // 如果之前是点踩，改为点赞
                like.setType(Like.LikeType.LIKE);
                likeRepository.save(like);
                return;
            }
        }

        // 创建新的点赞记录
        Like like = new Like();
        like.setUser(user);
        like.setArticle(article);
        like.setType(Like.LikeType.LIKE);
        likeRepository.save(like);
    }

    /**
     * 取消点赞文章
     */
    public void unlikeArticle(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        // 查找点赞记录
        Like like = likeRepository.findByUserAndArticle(user, article)
                .orElseThrow(() -> new BlogException("您尚未点赞此文章", HttpStatus.NOT_FOUND));

        // 删除点赞记录
        likeRepository.delete(like);
    }

    /**
     * 检查用户是否已点赞文章
     */
    @Transactional(readOnly = true)
    public boolean isArticleLikedByUser(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BlogException("用户不存在", HttpStatus.NOT_FOUND));

        return likeRepository.existsByUserAndArticleAndLike(user, article);
    }

    /**
     * 获取文章的点赞数
     */
    @Transactional(readOnly = true)
    public int getArticleLikeCount(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        return likeRepository.countByArticle(article);
    }
}

