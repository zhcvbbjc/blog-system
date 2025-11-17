package com.blog.util;

import com.blog.exception.BlogException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Slf4j
public class FileUploadUtil {

    // 允许的图片类型
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp"
    );

    // 允许的文件类型
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp",
            "application/pdf", "text/plain", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    // 文件大小限制（10MB）
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 图片大小限制（5MB）
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    /**
     * 验证图片文件
     */
    public static void validateImageFile(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES, MAX_IMAGE_SIZE, "图片");
    }

    /**
     * 验证普通文件
     */
    public static void validateFile(MultipartFile file) {
        validateFile(file, ALLOWED_FILE_TYPES, MAX_FILE_SIZE, "文件");
    }

    /**
     * 通用文件验证
     */
    private static void validateFile(MultipartFile file, List<String> allowedTypes,
                                     long maxSize, String fileTypeName) {
        if (file == null || file.isEmpty()) {
            throw new BlogException(fileTypeName + "不能为空", HttpStatus.BAD_REQUEST);
        }

        // 检查文件大小
        if (file.getSize() > maxSize) {
            String sizeMB = String.format("%.2f", maxSize / (1024.0 * 1024.0));
            throw new BlogException(
                    fileTypeName + "大小不能超过 " + sizeMB + "MB",
                    HttpStatus.BAD_REQUEST
            );
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new BlogException(
                    "不支持的文件类型: " + contentType,
                    HttpStatus.BAD_REQUEST
            );
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new BlogException("文件名不合法", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 生成唯一的文件名
     */
    public static String generateUniqueFilename(String originalFilename) {
        String fileExtension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + fileExtension;
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * 创建目录（如果不存在）
     */
    public static void createDirectoryIfNotExists(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
            log.debug("创建目录: {}", directory);
        }
    }

    /**
     * 安全保存文件
     */
    public static String saveFile(MultipartFile file, String baseDir, String subDir) throws IOException {
        // 创建目录
        Path uploadDir = Paths.get(baseDir, subDir);
        createDirectoryIfNotExists(uploadDir);

        // 生成唯一文件名
        String filename = generateUniqueFilename(file.getOriginalFilename());
        Path filePath = uploadDir.resolve(filename);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("文件保存成功: {}", filePath);

        return String.format("/uploads/%s/%s", subDir, filename);
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String fileUrl, String baseDir) {
        if (fileUrl == null || !fileUrl.startsWith("/uploads/")) {
            return false;
        }

        try {
            // 从URL中提取文件路径
            String relativePath = fileUrl.substring("/uploads/".length());
            Path filePath = Paths.get(baseDir, relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("文件删除成功: {}", filePath);
                return true;
            }
        } catch (IOException e) {
            log.error("删除文件失败: {}", e.getMessage());
        }

        return false;
    }

    /**
     * 获取文件MIME类型
     */
    public static String getMimeType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();

        switch (extension) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".webp":
                return "image/webp";
            case ".bmp":
                return "image/bmp";
            case ".pdf":
                return "application/pdf";
            case ".txt":
                return "text/plain";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 检查是否为图片文件
     */
    public static boolean isImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp").contains(extension);
    }

    /**
     * 获取文件大小的人类可读格式
     */
    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    /**
     * 清理临时文件
     */
    public static void cleanupTempFiles(Path tempDir, long maxAgeMillis) {
        try {
            if (!Files.exists(tempDir)) {
                return;
            }

            long currentTime = System.currentTimeMillis();

            Files.list(tempDir)
                    .filter(path -> {
                        try {
                            long fileAge = currentTime - Files.getLastModifiedTime(path).toMillis();
                            return fileAge > maxAgeMillis;
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                            log.debug("删除临时文件: {}", path);
                        } catch (IOException e) {
                            log.warn("无法删除临时文件: {}", path);
                        }
                    });

        } catch (IOException e) {
            log.error("清理临时文件失败: {}", e.getMessage());
        }
    }
}