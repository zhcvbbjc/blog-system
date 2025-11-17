package com.blog.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户个人信息请求 DTO
 */
@Data
public class UpdateProfileRequest {

    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Size(max = 200, message = "个人简介不能超过200个字符")
    private String bio;
}