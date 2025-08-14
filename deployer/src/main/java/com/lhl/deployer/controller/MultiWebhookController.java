package com.lhl.deployer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhl.deployer.config.WebHookListConfig;
import com.lhl.deployer.result.R;
import com.lhl.deployer.result.ResultCode;
import com.lhl.deployer.service.ReleaseWebhookService;
import com.lhl.deployer.util.SignatureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/8/14_22:24
 */
@RestController
public class MultiWebhookController {

    @Autowired
    private WebHookListConfig webHookListConfig;

    @Autowired
    private ReleaseWebhookService releaseWebhookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/webhook/{id}")
    public R<?> webhook(
            @PathVariable String id,
            @RequestHeader(value = "X-Hub-Signature-256") String signature,
            @RequestHeader(value = "X-GitHub-Event") String event,
            @RequestBody String payload
    ) {
        System.out.println("执行webhook, ID: " + id);
        // 1. 找到对应配置
        WebHookListConfig.WebhookProperty config = null;
        for (WebHookListConfig.WebhookProperty property : webHookListConfig.getList()) {
            if (Objects.equals(property.getId(), id)) {
                config = property;
                break;
            }
        }
        if (config == null) {
            System.out.println("找不到 Webhook ID: " + id);
            return R.error(ResultCode.NOT_FOUND, "Unknown webhook id: " + id);
        }

        // 2. 校验签名
        if (signature == null || !SignatureUtil.verify(payload.getBytes(StandardCharsets.UTF_8), signature, config.getSecret())) {
            System.out.println("校验签名失败！");
            return R.error(ResultCode.FORBIDDEN, "Invalid signature");
        }

        // 3. 处理 release:edited 请求
        try {
            if (!"release".equalsIgnoreCase(event) || !"edited".equals(objectMapper.readTree(payload).path("action").asText())) {
                throw new IllegalArgumentException("Invalid payload");
            }
        } catch (Exception e) {
            System.out.println("不是 release:edited 请求，忽略: " + event);
            return R.ok(null, "不是 release:edited 请求，忽略: " + event);
        }

        // 4. 处理逻辑
        try {
            releaseWebhookService.release(id, config, payload);
            return R.ok(null);
        } catch (Exception e) {
            if ("ignore".equals(e.getMessage())) {
                return R.ok(null, "assets没有文件，忽略");
            }
            System.out.println("处理失败");
            e.printStackTrace();
            return R.error(ResultCode.SERVER_ERROR, e.getMessage());
        }
    }
}
