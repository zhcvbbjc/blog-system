package com.blog.dto.response;

import com.blog.entity.Conversation;
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

    public ConversationResponse() {}

    // 👇 必须有这个静态方法！
    public static ConversationResponse from(Conversation conversation) {
        ConversationResponse response = new ConversationResponse();
        response.id = conversation.getId();
        response.title = conversation.getTitle();
        response.type = conversation.getType();
        response.updatedAt = conversation.getUpdatedAt();
        return response;
    }

    // getters and setters (or use Lombok @Data)
}

