package com.blog.util;

import lombok.extern.slf4j.Slf4j;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Slug 生成工具类
 * 用于生成URL友好的字符串
 */
@Slf4j
public class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGE_DASHES = Pattern.compile("(^-|-$)");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-{2,}");
    private static final Pattern NON_WORD_CHARS = Pattern.compile("[^\\p{L}\\p{N}_-]");

    /**
     * 从文本生成slug
     */
    public static String generateSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("输入文本不能为空");
        }

        log.debug("生成slug，原始文本: {}", text);

        // 转换为小写
        String slug = text.toLowerCase().trim();

        // 处理中文：将中文转换为拼音或保留（这里简单处理，实际项目中可能需要拼音转换）
        slug = handleChineseCharacters(slug);

        // 替换空格为连字符
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // 移除重音符号
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // 移除非单词字符（保留连字符和下划线）
        slug = NON_WORD_CHARS.matcher(slug).replaceAll("");

        // 处理连续的连字符
        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");

        // 移除开头和结尾的连字符
        slug = EDGE_DASHES.matcher(slug).replaceAll("");

        // 如果slug为空，生成随机slug
        if (slug.isEmpty()) {
            slug = generateRandomSlug();
        }

        // 限制长度
        slug = truncateSlug(slug, 100);

        log.debug("生成slug结果: {}", slug);
        return slug;
    }

    /**
     * 处理中文字符
     * 注意：这是一个简单实现，实际项目中可能需要集成拼音转换库
     */
    private static String handleChineseCharacters(String text) {
        // 这里简单移除中文字符，实际项目中应该转换为拼音
        // 例如使用 pinyin4j 库

        // 临时解决方案：保留中文字符，但在slug中使用unicode或移除
        // 这里选择移除中文字符，因为很多系统不支持中文slug
        String result = text.replaceAll("[\\u4e00-\\u9fa5]", "");

        // 如果移除中文后为空，添加前缀
        if (result.trim().isEmpty()) {
            result = "post-" + System.currentTimeMillis();
        }

        return result;
    }

    /**
     * 生成随机slug（用于处理特殊情况）
     */
    private static String generateRandomSlug() {
        return "post-" + System.currentTimeMillis() + "-" +
                Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    /**
     * 截断slug到指定长度
     */
    private static String truncateSlug(String slug, int maxLength) {
        if (slug.length() <= maxLength) {
            return slug;
        }

        // 尝试在单词边界处截断
        int lastDash = slug.lastIndexOf('-', maxLength);
        if (lastDash > maxLength * 0.7) { // 确保不会截断太多
            return slug.substring(0, lastDash);
        }

        return slug.substring(0, maxLength);
    }

    /**
     * 生成唯一的slug（添加后缀确保唯一性）
     */
    public static String generateUniqueSlug(String text, SlugChecker slugChecker) {
        String baseSlug = generateSlug(text);
        String uniqueSlug = baseSlug;

        int counter = 1;
        while (slugChecker.exists(uniqueSlug)) {
            uniqueSlug = baseSlug + "-" + counter;
            counter++;

            // 防止无限循环
            if (counter > 100) {
                throw new IllegalStateException("无法生成唯一的slug");
            }
        }

        return uniqueSlug;
    }

    /**
     * 验证slug是否有效
     */
    public static boolean isValidSlug(String slug) {
        if (slug == null || slug.isEmpty() || slug.length() > 100) {
            return false;
        }

        // 只允许字母、数字、连字符和下划线
        return slug.matches("^[a-z0-9_-]+$");
    }

    /**
     * 清理和修复slug
     */
    public static String cleanSlug(String slug) {
        if (slug == null) {
            return null;
        }

        String cleaned = slug.toLowerCase().trim();
        cleaned = NON_WORD_CHARS.matcher(cleaned).replaceAll("");
        cleaned = MULTIPLE_DASHES.matcher(cleaned).replaceAll("-");
        cleaned = EDGE_DASHES.matcher(cleaned).replaceAll("");

        return cleaned.isEmpty() ? generateRandomSlug() : cleaned;
    }

    /**
     * Slug检查器接口（用于检查slug是否已存在）
     */
    @FunctionalInterface
    public interface SlugChecker {
        boolean exists(String slug);
    }

    /**
     * 从文件名生成slug
     */
    public static String slugFromFilename(String filename) {
        if (filename == null) {
            return generateRandomSlug();
        }

        // 移除文件扩展名
        String nameWithoutExtension = filename;
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            nameWithoutExtension = filename.substring(0, dotIndex);
        }

        return generateSlug(nameWithoutExtension);
    }

    /**
     * 生成SEO友好的slug
     */
    public static String generateSEOFriendlySlug(String title, String... keywords) {
        StringBuilder slugBuilder = new StringBuilder();

        // 添加标题
        slugBuilder.append(generateSlug(title));

        // 添加关键词（如果有）
        if (keywords != null && keywords.length > 0) {
            for (String keyword : keywords) {
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String keywordSlug = generateSlug(keyword);
                    if (!slugBuilder.toString().contains(keywordSlug)) {
                        slugBuilder.append("-").append(keywordSlug);
                    }
                }
            }
        }

        String result = slugBuilder.toString();

        // 限制总长度
        return truncateSlug(result, 120);
    }
}