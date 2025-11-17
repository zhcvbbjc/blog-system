package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域访问
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",  // React 开发服务器
                        "http://localhost:8080",  // 本应用
                        "http://127.0.0.1:3000",
                        "http://127.0.0.1:8080"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);  // 预检请求缓存时间（秒）
    }

    /**
     * 配置静态资源处理
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 处理文件上传的静态资源访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");

        // 如果使用 Swagger，添加资源处理器
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    // 如果需要自定义 JSON 序列化/反序列化，可以在这里配置
    // 例如：配置全局的日期格式

    // @Override
    // public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    //     // 自定义消息转换器
    // }
}