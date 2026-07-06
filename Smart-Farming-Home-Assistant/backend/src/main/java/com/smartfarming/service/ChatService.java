package com.smartfarming.service;

import com.smartfarming.dto.ChatRequest;
import com.smartfarming.dto.ChatResponse;
import com.smartfarming.entity.ChatMessage;
import com.smartfarming.repository.ChatMessageRepository;
import com.smartfarming.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QwenAIService qwenAIService;

    public ChatResponse sendMessage(ChatRequest request) {
        Long userId = getCurrentUserId();

        ChatMessage userMessage = ChatMessage.builder()
                .userId(userId)
                .role("user")
                .content(request.getContent())
                .build();
        chatMessageRepository.save(userMessage);

        List<ChatMessage> allMessages = chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);
        List<ChatMessage> recentHistory = allMessages.stream()
                .skip(Math.max(0, allMessages.size() - 20L))
                .collect(Collectors.toList());

        List<QwenAIService.ChatMessage> aiHistory = recentHistory.stream()
                .map(msg -> new QwenAIService.ChatMessage(msg.getRole(), msg.getContent()))
                .collect(Collectors.toList());

        String reply = qwenAIService.chat(request.getContent(), aiHistory);
        String expression = determineExpression(request.getContent(), reply);

        ChatMessage botMessage = ChatMessage.builder()
                .userId(userId)
                .role("bot")
                .content(reply)
                .expression(expression)
                .build();
        chatMessageRepository.save(botMessage);

        ChatResponse response = new ChatResponse();
        response.setContent(reply);
        response.setExpression(expression);
        return response;
    }

    public List<Map<String, Object>> getChatHistory() {
        Long userId = getCurrentUserId();
        List<ChatMessage> messages = chatMessageRepository.findByUserIdOrderByTimestampAsc(userId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatMessage msg : messages) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", msg.getId());
            item.put("role", msg.getRole());
            item.put("content", msg.getContent());
            item.put("expression", msg.getExpression());
            item.put("timestamp", msg.getTimestamp().format(formatter));
            result.add(item);
        }
        return result;
    }

    public void clearChatHistory() {
        Long userId = getCurrentUserId();
        chatMessageRepository.deleteByUserId(userId);
    }

    private String determineExpression(String userMessage, String botReply) {
        String lower = userMessage.toLowerCase();
        if (lower.contains("你好") || lower.contains("嗨") || lower.contains("谢谢") || lower.contains("感谢")) {
            return "happy";
        }
        if (lower.contains("问题") || lower.contains("怎么办") || lower.contains("为什么")) {
            return "thinking";
        }
        if (lower.contains("紧急") || lower.contains("救命") || lower.contains("坏了") || lower.contains("死了")) {
            return "surprised";
        }
        if (lower.contains("不知道") || lower.contains("算了") || lower.contains("不行") || lower.contains("失败")) {
            return "sad";
        }
        return "normal";
    }

    private Long getCurrentUserId() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }
}
