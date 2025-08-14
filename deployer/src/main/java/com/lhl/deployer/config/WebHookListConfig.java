package com.lhl.deployer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/8/14_22:17
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "webhooks")
public class WebHookListConfig {
    private List<WebhookProperty> list;

    @Data
    public static class WebhookProperty {
        private String id;
        private String secret;
        private String distPath;
        private String tempPath;
        private Replace replace = new Replace();

        @Data
        public static class Replace {
            private boolean enabled;
            private String target;
            private String replacement;
        }
    }
}
