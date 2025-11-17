package com.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = "articles")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(name = "usage_count", nullable = false)
    private Integer usageCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 关联关系
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Article> articles = new HashSet<>();

    // 业务方法
    public void incrementUsageCount() {
        this.usageCount++;
    }

    public void decrementUsageCount() {
        if (this.usageCount > 0) {
            this.usageCount--;
        }
    }

    // 根据名称创建标签（工厂方法）
    public static Tag create(String name) {
        Tag tag = new Tag();
        tag.setName(name.trim().toLowerCase());
        return tag;
    }

    // 检查标签名称是否有效
    public static boolean isValidTagName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // 标签名称只允许字母、数字、中文和连字符
        return name.matches("^[\\w\\-\\u4e00-\\u9fa5]+$");
    }
}