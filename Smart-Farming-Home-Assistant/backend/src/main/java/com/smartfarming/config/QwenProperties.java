package com.smartfarming.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai.qwen")
public class QwenProperties {

    private String apiKey;
    private String model;
    private String apiHost;
    private String baseUrl;
    private String workspaceId;
}
