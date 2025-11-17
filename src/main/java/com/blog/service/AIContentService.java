package com.blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;          // 重点：你缺少的就是这个
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIContentService {

    private final ChatClient chatClient;

    @Value("${blog.ai.enabled:true}")
    private boolean aiEnabled;

    @Value("${blog.ai.auto-generate-summary:true}")
    private boolean autoGenerateSummary;

    @Value("${blog.ai.auto-generate-tags:true}")
    private boolean autoGenerateTags;

    /**
     * 生成文章摘要
     */
    public String generateArticleSummary(String content) {
        if (!aiEnabled || !autoGenerateSummary) {
            return null;
        }

        try {
            String truncatedContent = truncateContent(content, 2000);

            String promptTemplate = """
                    请为以下博客文章生成一个简洁的摘要，要求：
                    1. 长度在100-200字之间
                    2. 突出文章的核心观点和主要内容
                    3. 语言简洁明了，具有吸引力
                    4. 使用中文回复

                    文章内容：
                    {content}
                    """;

            Prompt prompt = new PromptTemplate(promptTemplate)
                    .create(Map.of("content", truncatedContent));

            String summary = chatClient.call(prompt)
                    .getResult()
                    .getOutput()
                    .getContent();

            log.info("AI生成摘要成功，长度: {}", summary.length());
            return summary.trim();

        } catch (Exception e) {
            log.error("AI生成摘要失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成文章标签
     */
    public Set<String> generateTags(String content) {
        if (!aiEnabled || !autoGenerateTags) {
            return new HashSet<>();
        }

        try {
            String truncatedContent = truncateContent(content, 1500);

            String promptTemplate = """
                    请为以下博客文章生成3-5个相关标签，要求：
                    1. 标签用中文逗号分隔
                    2. 每个标签长度不超过10个字符
                    3. 标签要能准确反映文章主题
                    4. 避免使用过于宽泛的标签

                    文章内容：
                    {content}
                    """;

            Prompt prompt = new PromptTemplate(promptTemplate)
                    .create(Map.of("content", truncatedContent));

            String tagsResponse = chatClient.call(prompt)
                    .getResult()
                    .getOutput()
                    .getContent();

            Set<String> tags = parseTags(tagsResponse);
            log.info("AI生成标签成功: {}", tags);
            return tags;

        } catch (Exception e) {
            log.error("AI生成标签失败: {}", e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * 生成SEO优化建议
     */
    public String generateSEOSuggestions(String title, String content) {
        if (!aiEnabled) {
            return null;
        }

        try {
            String truncatedContent = truncateContent(content, 1500);

            String promptTemplate = """
                    请为以下博客文章提供SEO优化建议，包括：
                    1. 关键词建议
                    2. 元描述优化
                    3. 标题优化建议
                    4. 内容结构建议

                    文章标题：{title}
                    文章内容：{content}
                    """;

            Prompt prompt = new PromptTemplate(promptTemplate)
                    .create(Map.of("title", title, "content", truncatedContent));

            String suggestions = chatClient.call(prompt)
                    .getResult()
                    .getOutput()
                    .getContent();

            log.info("AI生成SEO建议成功");
            return suggestions.trim();

        } catch (Exception e) {
            log.error("AI生成SEO建议失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 内容质量检查
     */
    public String checkContentQuality(String content) {
        if (!aiEnabled) {
            return null;
        }

        try {
            String truncatedContent = truncateContent(content, 2000);

            String promptTemplate = """
                    请检查以下博客文章的内容质量，提供改进建议：
                    1. 语法和拼写检查
                    2. 逻辑结构建议
                    3. 内容完整性评估
                    4. 可读性改进建议

                    文章内容：
                    {content}
                    """;

            Prompt prompt = new PromptTemplate(promptTemplate)
                    .create(Map.of("content", truncatedContent));

            String qualityReport = chatClient.call(prompt)
                    .getResult()
                    .getOutput()
                    .getContent();

            log.info("AI内容质量检查完成");
            return qualityReport.trim();

        } catch (Exception e) {
            log.error("AI内容质量检查失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 检查AI服务是否可用
     */
    public boolean isAIServiceAvailable() {
        if (!aiEnabled) {
            return false;
        }

        try {
            Prompt testPrompt = new Prompt("你好");

            String response = chatClient.call(testPrompt)
                    .getResult()
                    .getOutput()
                    .getContent();

            return response != null && !response.trim().isEmpty();

        } catch (Exception e) {
            log.warn("AI服务不可用: {}", e.getMessage());
            return false;
        }
    }

    private Set<String> parseTags(String tagsResponse) {
        Set<String> tags = new HashSet<>();

        if (tagsResponse == null || tagsResponse.trim().isEmpty()) {
            return tags;
        }

        String[] tagArray = tagsResponse.split("[,，\\n]");

        for (String tag : tagArray) {
            String cleanedTag = tag.trim();
            cleanedTag = cleanedTag.replaceAll("[^\\u4e00-\\u9fa5a-zA-Z]", "");

            if (!cleanedTag.isEmpty() && cleanedTag.length() <= 10) {
                tags.add(cleanedTag);
            }

            if (tags.size() >= 5) {
                break;
            }
        }

        return tags;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) {
            return "";
        }

        if (content.length() <= maxLength) {
            return content;
        }

        int lastPeriod = content.lastIndexOf('。', maxLength);
        int lastComma = content.lastIndexOf('，', maxLength);
        int lastSpace = content.lastIndexOf(' ', maxLength);

        int truncateIndex = Math.max(Math.max(lastPeriod, lastComma), lastSpace);
        if (truncateIndex > maxLength * 0.8) {
            return content.substring(0, truncateIndex + 1);
        }

        return content.substring(0, maxLength);
    }
}
