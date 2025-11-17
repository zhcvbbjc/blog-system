package com.blog.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 统一错误响应格式
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success = false;
    private String errorCode;
    private String message;
    private HttpStatus status;
    private Integer statusCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String path;
    private String detail;
    private List<FieldError> fieldErrors;
    private String stackTrace;

    /**
     * 基础构造函数
     */
    public ErrorResponse(String errorCode, String message, HttpStatus status, String path) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }

    /**
     * 包含详细信息的构造函数
     */
    public ErrorResponse(String errorCode, String message, HttpStatus status, String path, String detail) {
        this(errorCode, message, status, path);
        this.detail = detail;
    }

    /**
     * 包含字段错误的构造函数
     */
    public ErrorResponse(String errorCode, String message, HttpStatus status, String path, List<FieldError> fieldErrors) {
        this(errorCode, message, status, path);
        this.fieldErrors = fieldErrors;
    }

    /**
     * 从 BlogException 创建 ErrorResponse
     */
    public static ErrorResponse fromBlogException(BlogException ex, String path) {
        ErrorResponse response = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus(),
                path
        );
        response.setDetail(ex.getDetail());
        return response;
    }

    /**
     * 字段错误信息
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;
        private String code;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public FieldError(String field, String message, Object rejectedValue, String code) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
            this.code = code;
        }
    }

    /**
     * 构建器模式，便于创建 ErrorResponse
     */
    public static class Builder {
        private String errorCode;
        private String message;
        private HttpStatus status;
        private String path;
        private String detail;
        private List<FieldError> fieldErrors;
        private String stackTrace;

        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public Builder fieldErrors(List<FieldError> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public Builder stackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
            return this;
        }

        public ErrorResponse build() {
            ErrorResponse response = new ErrorResponse(errorCode, message, status, path);
            response.setDetail(detail);
            response.setFieldErrors(fieldErrors);
            response.setStackTrace(stackTrace);
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return String.format("ErrorResponse{success=%s, errorCode='%s', message='%s', status=%s, path='%s'}",
                success, errorCode, message, status, path);
    }
}