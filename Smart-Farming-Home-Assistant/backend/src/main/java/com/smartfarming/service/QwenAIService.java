package com.smartfarming.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smartfarming.config.QwenProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class QwenAIService {

    private static final Logger logger = LoggerFactory.getLogger(QwenAIService.class);

    @Autowired
    private QwenProperties qwenProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private final HttpClient httpClient;

    public QwenAIService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String chat(String userMessage, List<ChatMessage> history) {
        try {
            String url = qwenProperties.getBaseUrl() + "/chat/completions";

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", qwenProperties.getModel());

            ArrayNode messages = requestBody.putArray("messages");

            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一位专业的智慧农业助手，名叫小禾。你的职责是帮助用户管理家庭农场，解答农业相关问题，提供种植建议、病虫害防治指导、环境数据分析等。请用亲切、专业的语气回答，回答要简洁实用，适当使用emoji。");
            messages.add(systemMsg);

            if (history != null) {
                for (ChatMessage msg : history) {
                    ObjectNode historyMsg = objectMapper.createObjectNode();
                    historyMsg.put("role", msg.role);
                    historyMsg.put("content", msg.content);
                    messages.add(historyMsg);
                }
            }

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + qwenProperties.getApiKey())
                    .header("X-DashScope-WorkSpace", qwenProperties.getWorkspaceId())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .timeout(Duration.ofSeconds(60))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode responseJson = objectMapper.readTree(response.body());
                return responseJson.path("choices").get(0).path("message").path("content").asText();
            } else {
                logger.error("千问API调用失败，状态码: {}, 响应: {}", response.statusCode(), response.body());
                return getFallbackReply(userMessage);
            }
        } catch (Exception e) {
            logger.error("调用千问API异常", e);
            return getFallbackReply(userMessage);
        }
    }

    private String getFallbackReply(String userMessage) {
        String lower = userMessage.toLowerCase();
        if (lower.contains("你好") || lower.contains("嗨") || lower.contains("hello")) {
            return "您好！我是智慧农业助手小禾，很高兴为您服务！🌾\n\n我可以帮您：\n• 分析环境数据\n• 提供种植建议\n• 指导病虫害防治\n• 解答农业问题\n\n请问有什么需要帮助的吗？";
        }
        if (lower.contains("谢谢") || lower.contains("感谢")) {
            return "不客气！很高兴能帮到您！😊\n\n如果有其他问题，随时可以问我。祝您的农场丰收顺利！🌾✨";
        }
        return "抱歉，AI服务暂时不可用。请稍后再试，或查看帮助中心获取更多信息。🌱";
    }

    public static class ChatMessage {
        public String role;
        public String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
