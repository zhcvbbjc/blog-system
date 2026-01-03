package com.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private String title;
    private String type;
    private LocalDateTime updatedAt;
}

