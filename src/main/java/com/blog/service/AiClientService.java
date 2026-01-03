package com.blog.service;

import com.blog.entity.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiClientService {

    private final ChatClient chatClient;

    public String chat(List<Message> history) {

        List<org.springframework.ai.chat.messages.Message> messages = new ArrayList<>();

        // System prompt
        messages.add(new SystemMessage("""
        你是一个金融知识助手，服务于一个金融博客系统。
        请用简体中文回答用户问题，内容专业、简洁、可靠。
        """));

        for (Message msg : history) {
            if ("AI".equalsIgnoreCase(msg.getSenderType())) {
                messages.add(new AssistantMessage(msg.getContent()));
            } else {
                messages.add(new UserMessage(msg.getContent()));
            }
        }

        Prompt prompt = new Prompt(messages);

        ChatResponse response = chatClient.call(prompt);

        return response.getResult().getOutput().getContent();
    }
}