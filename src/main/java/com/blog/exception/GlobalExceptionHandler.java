package com.blog.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${app.environment:production}")
    private String environment;

    /** ======================== 自定义异常 ======================== */
    @ExceptionHandler(BlogException.class)
    public ResponseEntity<ErrorResponse> handleBlogException(BlogException ex, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.fromBlogException(ex, getRequestPath(request));

        if (isDevelopment()) {
            errorResponse.setStackTrace(getStackTrace(ex));
        }

        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    /** ======================== 参数校验失败（@Valid） ======================== */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("参数验证失败: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ErrorResponse.FieldError(
                    fieldError.getField(),
                    fieldError.getDefaultMessage(),
                    fieldError.getRejectedValue(),
                    fieldError.getCode()
            ));
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message("请求参数验证失败")
                .status(HttpStatus.BAD_REQUEST)
                .path(getRequestPath(request))
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /** ======================== 参数违法（@RequestParam） ======================== */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        log.warn("约束违反异常: {}", ex.getMessage());

        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            fieldErrors.add(new ErrorResponse.FieldError(
                    violation.getPropertyPath().toString(),
                    violation.getMessage(),
                    violation.getInvalidValue()
            ));
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("VALIDATION_ERROR")
                .message("请求参数验证失败")
                .status(HttpStatus.BAD_REQUEST)
                .path(getRequestPath(request))
                .fieldErrors(fieldErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /** ======================== 登录失败 ======================== */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
            BadCredentialsException ex, HttpServletRequest request) {

        log.warn("认证失败: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .errorCode("AUTHENTICATION_FAILED")
                .message("用户名或密码错误")
                .status(HttpStatus.UNAUTHORIZED)
                .path(getRequestPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /** ======================== 权限不足 ======================== */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {

        log.warn("访问被拒绝: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .errorCode("ACCESS_DENIED")
                .message("没有权限访问该资源")
                .status(HttpStatus.FORBIDDEN)
                .path(getRequestPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /** ======================== 未认证访问 ======================== */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException ex, HttpServletRequest request) {

        log.warn("认证不足: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
                .errorCode("AUTHENTICATION_REQUIRED")
                .message("需要认证才能访问该资源")
                .status(HttpStatus.UNAUTHORIZED)
                .path(getRequestPath(request))
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /** ======================== JSON 解析失败 ======================== */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        log.warn("JSON解析失败: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("MALFORMED_JSON")
                .message("请求JSON格式错误")
                .status(HttpStatus.BAD_REQUEST)
                .path(getRequestPath(request))
                .detail("请检查JSON格式是否正确")
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /** ======================== 兜底异常 ======================== */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception ex, HttpServletRequest request) {

        log.error("未处理的异常: ", ex);

        ErrorResponse response = ErrorResponse.builder()
                .errorCode("INTERNAL_ERROR")
                .message("服务器内部错误")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .path(getRequestPath(request))
                .build();

        if (isDevelopment()) {
            response.setDetail(ex.getMessage());
            response.setStackTrace(getStackTrace(ex));
        } else {
            response.setDetail("请稍后重试或联系管理员");
        }

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** ======================== Spring MVC 内部异常 ======================== */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        log.warn("Spring MVC异常: {} - {}", status, ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode("SPRING_MVC_ERROR")
                .message(ex.getMessage())
                .status(HttpStatus.valueOf(status.value()))
                .path(getRequestPath(request))
                .build();

        if (isDevelopment()) {
            errorResponse.setStackTrace(getStackTrace(ex));
        }

        return new ResponseEntity<>(errorResponse, headers, HttpStatus.valueOf(status.value()));
    }

    /** ======================== 工具方法 ======================== */
    private String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    private String getRequestPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "unknown";
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder(ex.toString()).append("\n");
        for (StackTraceElement e : ex.getStackTrace()) {
            sb.append("\tat ").append(e).append("\n");
        }
        return sb.toString();
    }

    private boolean isDevelopment() {
        return environment.equalsIgnoreCase("dev")
                || environment.equalsIgnoreCase("development")
                || environment.equalsIgnoreCase("local");
    }
}
