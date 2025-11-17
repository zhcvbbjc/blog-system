package com.blog.service;

import com.blog.dto.request.CommentRequest;
import com.blog.dto.response.CommentResponse;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.exception.BlogException;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    /**
     * 获取文章的所有评论
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByArticleId(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        List<Comment> comments = commentRepository.findByArticleAndParentIsNullOrderByCreatedAtDesc(article);

        return comments.stream()
                .map(comment -> buildCommentResponseWithReplies(comment))
                .collect(Collectors.toList());
    }

    /**
     * 创建评论
     */
    public CommentResponse createComment(Long articleId, CommentRequest request, User user) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new BlogException("文章不存在", HttpStatus.NOT_FOUND));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setArticle(article);

        Comment savedComment = commentRepository.save(comment);

        // 更新文章的评论计数
        article.incrementCommentCount();
        articleRepository.save(article);

        return CommentResponse.fromCommentWithAuthor(savedComment, user);
    }

    /**
     * 回复评论
     */
    public CommentResponse replyComment(Long parentId, CommentRequest request, User user) {
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new BlogException("评论不存在", HttpStatus.NOT_FOUND));

        Comment reply = new Comment();
        reply.setContent(request.getContent());
        reply.setUser(user);
        reply.setArticle(parentComment.getArticle());
        reply.setParent(parentComment);

        Comment savedReply = commentRepository.save(reply);

        // 更新文章的评论计数
        Article article = parentComment.getArticle();
        article.incrementCommentCount();
        articleRepository.save(article);

        return CommentResponse.fromCommentWithAuthor(savedReply, user);
    }

    /**
     * 删除评论
     */
    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BlogException("评论不存在", HttpStatus.NOT_FOUND));

        // 检查权限：只能删除自己的评论
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BlogException("没有权限删除此评论", HttpStatus.FORBIDDEN);
        }

        Article article = comment.getArticle();

        // 如果是父评论，需要同时删除所有回复
        if (comment.isRootComment()) {
            // 减少评论计数（包括所有回复）
            int replyCount = comment.getReplies().size();
            for (int i = 0; i <= replyCount; i++) {
                article.decrementCommentCount();
            }
            commentRepository.delete(comment);
        } else {
            // 如果是回复，只删除当前评论
            article.decrementCommentCount();
            commentRepository.delete(comment);
        }

        articleRepository.save(article);
    }

    /**
     * 根据ID获取评论
     */
    @Transactional(readOnly = true)
    public CommentResponse getCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BlogException("评论不存在", HttpStatus.NOT_FOUND));

        return buildCommentResponseWithReplies(comment);
    }

    /**
     * 获取用户的所有评论
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return comments.stream()
                .map(comment -> CommentResponse.fromCommentWithAuthor(comment, comment.getUser()))
                .collect(Collectors.toList());
    }

    /**
     * 构建包含回复的评论响应
     */
    private CommentResponse buildCommentResponseWithReplies(Comment comment) {
        List<CommentResponse> replyResponses = comment.getReplies().stream()
                .map(reply -> CommentResponse.fromCommentWithAuthor(reply, reply.getUser()))
                .collect(Collectors.toList());

        return CommentResponse.fromCommentWithAuthorAndReplies(comment, comment.getUser(), replyResponses);
    }
}