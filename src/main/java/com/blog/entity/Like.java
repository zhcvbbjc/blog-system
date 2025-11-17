package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "likes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "article_id"})
})
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"user", "article"})
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LikeType type = LikeType.LIKE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // 业务方法
    public boolean isLike() {
        return type == LikeType.LIKE;
    }

    public boolean isDislike() {
        return type == LikeType.DISLIKE;
    }

    // 点赞类型枚举
    public enum LikeType {
        LIKE, DISLIKE
    }

    // 预持久化操作 - 更新文章的点赞计数
    @PrePersist
    public void prePersist() {
        if (article != null && isLike()) {
            article.incrementLikeCount();
        }
    }

    // 预删除操作 - 更新文章的点赞计数
    @PreRemove
    public void preRemove() {
        if (article != null && isLike()) {
            article.decrementLikeCount();
        }
    }
}