package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaAuditing  // 启用 JPA 审计功能
@EnableJpaRepositories(basePackages = "com.blog.repository")  // 指定 Repository 扫描路径
@EnableTransactionManagement  // 启用事务管理
public class JpaConfig {

    // 注意：@EnableJpaAuditing 也可以放在启动类上
    // 这里单独配置是为了更清晰的职责分离

    // 如果需要自定义 JPA 配置，可以在这里添加
    // 例如：自定义物理命名策略、自定义方言等
}