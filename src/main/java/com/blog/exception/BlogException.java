package com.blog.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 自定义博客系统异常
 */
@Getter
public class BlogException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String detail;

    /**
     * 基础构造函数
     */
    public BlogException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.detail = null;
    }

    /**
     * 包含错误码和HTTP状态的构造函数
     */
    public BlogException(String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = generateErrorCode(httpStatus);
        this.httpStatus = httpStatus;
        this.detail = null;
    }

    /**
     * 包含错误码、HTTP状态和详细信息的构造函数
     */
    public BlogException(String message, HttpStatus httpStatus, String detail) {
        super(message);
        this.errorCode = generateErrorCode(httpStatus);
        this.httpStatus = httpStatus;
        this.detail = detail;
    }

    /**
     * 包含错误码、HTTP状态、详细信息和根本原因的构造函数
     */
    public BlogException(String message, HttpStatus httpStatus, String detail, Throwable cause) {
        super(message, cause);
        this.errorCode = generateErrorCode(httpStatus);
        this.httpStatus = httpStatus;
        this.detail = detail;
    }

    /**
     * 自定义错误码的构造函数
     */
    public BlogException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.detail = null;
    }

    /**
     * 根据HTTP状态生成错误码
     */
    private String generateErrorCode(HttpStatus httpStatus) {
        if (httpStatus == null) {
            return "INTERNAL_ERROR";
        }

        switch (httpStatus) {
            case BAD_REQUEST:
                return "VALIDATION_ERROR";
            case UNAUTHORIZED:
                return "UNAUTHORIZED";
            case FORBIDDEN:
                return "ACCESS_DENIED";
            case NOT_FOUND:
                return "RESOURCE_NOT_FOUND";
            case CONFLICT:
                return "RESOURCE_CONFLICT";
            case UNPROCESSABLE_ENTITY:
                return "BUSINESS_ERROR";
            case TOO_MANY_REQUESTS:
                return "RATE_LIMIT_EXCEEDED";
            case INTERNAL_SERVER_ERROR:
                return "INTERNAL_ERROR";
            case SERVICE_UNAVAILABLE:
                return "SERVICE_UNAVAILABLE";
            default:
                return "UNKNOWN_ERROR";
        }
    }

    /**
     * 快速创建方法 - 资源未找到
     */
    public static BlogException notFound(String resourceName, Object identifier) {
        String message = String.format("%s 未找到 (ID: %s)", resourceName, identifier);
        return new BlogException(message, HttpStatus.NOT_FOUND);
    }

    /**
     * 快速创建方法 - 无权限访问
     */
    public static BlogException forbidden(String action) {
        String message = String.format("没有权限执行此操作: %s", action);
        return new BlogException(message, HttpStatus.FORBIDDEN);
    }

    /**
     * 快速创建方法 - 未授权
     */
    public static BlogException unauthorized(String message) {
        return new BlogException(message, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 快速创建方法 - 参数验证失败
     */
    public static BlogException validationError(String message) {
        return new BlogException(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 快速创建方法 - 资源冲突
     */
    public static BlogException conflict(String resourceName, String conflictReason) {
        String message = String.format("%s 冲突: %s", resourceName, conflictReason);
        return new BlogException(message, HttpStatus.CONFLICT);
    }

    /**
     * 快速创建方法 - 业务规则违反
     */
    public static BlogException businessError(String message) {
        return new BlogException(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    public String toString() {
        return String.format("BlogException{errorCode='%s', message='%s', httpStatus=%s, detail='%s'}",
                errorCode, getMessage(), httpStatus, detail);
    }
}