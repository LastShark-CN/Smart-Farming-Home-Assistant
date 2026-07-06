package com.smartfarming.controller;

import com.smartfarming.dto.ChatRequest;
import com.smartfarming.dto.ChatResponse;
import com.smartfarming.service.ChatService;
import com.smartfarming.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public Result<ChatResponse> sendMessage(@RequestBody @Valid ChatRequest request) {
        return Result.success(chatService.sendMessage(request));
    }

    @GetMapping("/history")
    public Result<List<Map<String, Object>>> getChatHistory() {
        return Result.success(chatService.getChatHistory());
    }

    @PostMapping("/clear")
    public Result<Void> clearChatHistory() {
        chatService.clearChatHistory();
        return Result.success("聊天记录已清空");
    }
}
