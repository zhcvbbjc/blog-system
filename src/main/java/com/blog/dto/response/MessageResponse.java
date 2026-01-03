package com.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private String senderType;
    private Long senderId;
    private String content;
    private LocalDateTime createdAt;
}
