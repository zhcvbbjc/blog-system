package com.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 博客系统启动类
 *
 * 使用 @SpringBootApplication 注解标记这是一个 Spring Boot 应用
 * 使用 @EnableJpaAuditing 注解启用 JPA 审计功能（自动设置创建时间、更新时间等）
 */
@SpringBootApplication
@EnableJpaAuditing
public class BlogApplication {

    /**
     * 应用主入口方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(BlogApplication.class, args);

        // 应用启动后的提示信息
        System.out.println("==========================================");
        System.out.println("🚀 个人博客系统启动成功!");
        System.out.println("📍 访问地址: http://localhost:8080");
        System.out.println("📚 API 文档: http://localhost:8080/swagger-ui.html (如果集成了 Swagger)");
        System.out.println("🔧 健康检查: http://localhost:8080/actuator/health");
        System.out.println("==========================================");
    }
}