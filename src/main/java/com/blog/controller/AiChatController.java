package com.blog.controller;

import com.blog.dto.request.ChatSendRequest;
import com.blog.dto.request.UpdateConversationRequest;
import com.blog.dto.response.ApiResponse;
import com.blog.dto.response.ConversationResponse;
import com.blog.dto.response.MessageResponse;
import com.blog.entity.Conversation;
import com.blog.entity.User;
import com.blog.security.CustomUserDetails;
import com.blog.service.AiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/conversations")
    public List<ConversationResponse> getConversationResponse(
            Authentication authentication
    ) {
        System.out.println("=== get chat history ===");

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.toUser();

        List<ConversationResponse> list =
                aiChatService.listMyAiConversations(user);

        System.out.println("history size = " + list.size());

        return list;
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

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {
        System.out.println("=== deleteConversation called ===");
        System.out.println("conversationId = " + conversationId);

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.toUser();
        aiChatService.deleteConversation(conversationId, user);
        System.out.println("删除对话成功");
        return ResponseEntity.ok(ApiResponse.success("对话删除成功", null));
    }

    @PatchMapping("/{conversationId}")
    public ConversationResponse updateConversationTitle(
            @PathVariable Long conversationId,
            @RequestBody UpdateConversationRequest request,   // 👈 专门的 DTO
            Authentication authentication
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.toUser();

        Conversation updated = aiChatService.modifiedAiConversationTitle(
                conversationId,
                user,
                request.getTitle()  // 👈 明确字段名
        );

        return ConversationResponse.from(updated);
    }
}
