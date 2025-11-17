package com.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * 创建或更新文章请求 DTO
 */
@Data
public class ArticleRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(min = 1, max = 200, message = "文章标题长度必须在1-200个字符之间")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    @Size(min = 10, message = "文章内容至少需要10个字符")
    private String content;

    @Size(max = 500, message = "文章摘要不能超过500个字符")
    private String summary;

    private Set<@Size(max = 20, message = "标签长度不能超过20个字符") String> tags;

    // 是否立即发布
    private Boolean publish = false;

    // 是否启用 AI 生成摘要
    private Boolean generateAISummary = true;

    // 是否启用 AI 生成标签
    private Boolean generateAITags = true;
}