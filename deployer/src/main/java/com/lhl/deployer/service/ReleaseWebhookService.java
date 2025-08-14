package com.lhl.deployer.service;

import com.lhl.deployer.config.WebHookListConfig;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/8/14_23:24
 */
public interface ReleaseWebhookService {

    void release(String id, WebHookListConfig.WebhookProperty config, String payload);
}
