package com.blog.controller;

import com.blog.dto.request.ChatSendRequest;
import com.blog.dto.response.ConversationResponse;
import com.blog.dto.response.MessageResponse;
import com.blog.entity.Conversation;
import com.blog.entity.User;
import com.blog.security.CustomUserDetails;
import com.blog.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping("/conversations")
    public ConversationResponse createConversation(Authentication authentication) {

        System.out.println("=== [AI] createConversation called ===");

        if (authentication == null) {
            System.out.println("❌ authentication is NULL");
            return null;
        }

        System.out.println("auth = " + authentication);
        System.out.println("principal = " + authentication.getPrincipal());

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.toUser();

        System.out.println("userId = " + user.getId());
        System.out.println("username = " + user.getUsername());

        Conversation c = aiChatService.createAiConversation(user);

        System.out.println("✅ created conversation:");
        System.out.println("id = " + c.getId());
        System.out.println("title = " + c.getTitle());
        System.out.println("type = " + c.getType());

        return new ConversationResponse(
                c.getId(),
                c.getTitle(),
                c.getType(),
                c.getUpdatedAt()
        );
    }

    @PostMapping("/{conversationId}/messages")
    public MessageResponse send(
            @PathVariable Long conversationId,
            @RequestBody ChatSendRequest request,
            Authentication authentication
    ) {
        System.out.println("=== send message ===");
        System.out.println("conversationId = " + conversationId);
        System.out.println("content = " + request.getContent());

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.toUser();

        return aiChatService.sendMessage(conversationId, user, request.getContent());
    }

    @GetMapping("/{conversationId}/messages")
    public List<MessageResponse> history(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {
        System.out.println("=== get history ===");
        System.out.println("conversationId = " + conversationId);

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.toUser();

        List<MessageResponse> list =
                aiChatService.getHistory(conversationId, user);

        System.out.println("history size = " + list.size());

        return list;
    }
}
