package com.blog.service;

import com.blog.dto.response.ConversationResponse;
import com.blog.dto.response.MessageResponse;
import com.blog.entity.Conversation;
import com.blog.entity.ConversationParticipant;
import com.blog.entity.Message;
import com.blog.entity.User;
import com.blog.repository.ConversationParticipantRepository;
import com.blog.repository.ConversationRepository;
import com.blog.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AiChatService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository participantRepository;
    private final MessageRepository messageRepository;
    private final AiClientService aiClientService;

    /**
     * 创建新的 AI 会话
     */
    public Conversation createAiConversation(User user) {

        if (user == null) {
            throw new RuntimeException("未登录用户不能创建 AI 会话");
        }

        // ✅ 关键修改点 ①：直接统计“该用户已有多少 AI 会话”
        long aiCount = conversationRepository.countAiByUserId(user.getId());

        String title = (aiCount == 0)
                ? "新建 AI 对话"
                : "新建 AI 对话-" + (aiCount + 1);

        Conversation c = new Conversation();
        c.setType("AI");
        c.setTitle(title);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());

        conversationRepository.save(c);

        ConversationParticipant cp = new ConversationParticipant();
        cp.setConversationId(c.getId());
        cp.setUserId(user.getId());
        cp.setRole("OWNER");
        cp.setJoinedAt(LocalDateTime.now());

        participantRepository.save(cp);

        return c;
    }

    /**
     * 查询当前用户的 AI 会话列表（左侧栏用）
     */
    public List<ConversationResponse> listMyAiConversations(User user) {

        return conversationRepository
                .findAiConversationsByUserId(user.getId())
                .stream()
                .map(c -> new ConversationResponse(
                        c.getId(),
                        c.getTitle(),
                        c.getType(),
                        c.getUpdatedAt()
                ))
                .toList();
    }

    /**
     * 发送消息
     */
    public MessageResponse sendMessage(
            Long conversationId,
            User user,
            String content
    ) {
        // 权限校验
        if (!participantRepository.existsByConversationIdAndUserId(conversationId, user.getId())) {
            throw new RuntimeException("无权访问该会话");
        }

        // 用户消息
        Message userMsg = new Message();
        userMsg.setConversationId(conversationId);
        userMsg.setSenderType("USER");
        userMsg.setSenderId(user.getId());
        userMsg.setContent(content);
        userMsg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(userMsg);

        // 历史上下文
        List<Message> history =
                messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        // AI 回复
        String aiReply = aiClientService.chat(history);

        Message aiMsg = new Message();
        aiMsg.setConversationId(conversationId);
        aiMsg.setSenderType("AI");
        aiMsg.setSenderId(null);
        aiMsg.setContent(aiReply);
        aiMsg.setCreatedAt(LocalDateTime.now());
        messageRepository.save(aiMsg);

        // 更新时间（影响左侧排序）
        conversationRepository.updateUpdatedAt(conversationId, LocalDateTime.now());

        return new MessageResponse(
                aiMsg.getId(),
                aiMsg.getSenderType(),
                aiMsg.getSenderId(),
                aiMsg.getContent(),
                aiMsg.getCreatedAt()
        );
    }

    /**
     * 获取历史消息
     */
    public List<MessageResponse> getHistory(Long conversationId, User user) {

        if (!participantRepository.existsByConversationIdAndUserId(conversationId, user.getId())) {
            throw new RuntimeException("无权访问该会话");
        }

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(m -> new MessageResponse(
                        m.getId(),
                        m.getSenderType(),
                        m.getSenderId(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }
}
