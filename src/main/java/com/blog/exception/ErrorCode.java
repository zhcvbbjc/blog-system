package com.blog.exception;

/**
 * 错误码枚举
 */
public enum ErrorCode {

    // 通用错误
    INTERNAL_ERROR("INTERNAL_ERROR", "内部服务器错误"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "服务暂时不可用"),

    // 认证授权错误
    UNAUTHORIZED("UNAUTHORIZED", "未授权访问"),
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "认证失败"),
    AUTHENTICATION_REQUIRED("AUTHENTICATION_REQUIRED", "需要认证"),
    ACCESS_DENIED("ACCESS_DENIED", "访问被拒绝"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "令牌已过期"),
    TOKEN_INVALID("TOKEN_INVALID", "令牌无效"),

    // 验证错误
    VALIDATION_ERROR("VALIDATION_ERROR", "参数验证失败"),
    MALFORMED_JSON("MALFORMED_JSON", "JSON格式错误"),

    // 资源错误
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "资源未找到"),
    RESOURCE_CONFLICT("RESOURCE_CONFLICT", "资源冲突"),
    RESOURCE_LIMIT_EXCEEDED("RESOURCE_LIMIT_EXCEEDED", "资源限制超出"),

    // 业务错误
    BUSINESS_ERROR("BUSINESS_ERROR", "业务规则违反"),
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", "操作不允许"),

    // 文件错误
    FILE_UPLOAD_ERROR("FILE_UPLOAD_ERROR", "文件上传失败"),
    FILE_NOT_FOUND("FILE_NOT_FOUND", "文件未找到"),
    FILE_TOO_LARGE("FILE_TOO_LARGE", "文件过大"),

    // 限流错误
    RATE_LIMIT_EXCEEDED("RATE_LIMIT_EXCEEDED", "请求频率过高");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据错误码获取枚举
     */
    public static ErrorCode fromCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.code.equals(code)) {
                return errorCode;
            }
        }
        return INTERNAL_ERROR;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}