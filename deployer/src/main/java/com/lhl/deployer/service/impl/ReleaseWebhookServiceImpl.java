package com.lhl.deployer.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhl.deployer.config.WebHookListConfig;
import com.lhl.deployer.service.ReleaseWebhookService;
import com.lhl.deployer.util.FileUtils;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class ReleaseWebhookServiceImpl implements ReleaseWebhookService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void release(String id, WebHookListConfig.WebhookProperty config, String payload) {
        try {
            // 1. 解析 payload 找下载 URL
            JsonNode root = objectMapper.readTree(payload);
            JsonNode assets = root.path("release").path("assets");
            if (!assets.isArray() || assets.isEmpty()) {
                throw new IllegalArgumentException("No assets found in release payload");
            }
            String downloadUrl = assets.get(0).path("browser_download_url").asText();
            System.out.println("已解析 payload 下载 URL：" + downloadUrl);

            // 2. 下载 ZIP 到临时文件
            Path tempZip = Path.of(config.getTempPath());
            FileUtils.downloadFile(downloadUrl, tempZip);
            System.out.println("已下载：" + tempZip);

            // 3. 解压 ZIP 到 distPath
            Path distDir = Path.of(config.getDistPath());
            FileUtils.unzip(tempZip, distDir);
            System.out.println("已解压：" + distDir);

            // 4. 如果开启替换功能，则替换 JS/CSS/HTML 文件中的字符串
            if (config.getReplace().isEnabled()) {
                FileUtils.replaceInJsFiles(
                        distDir,
                        config.getReplace().getTarget(),
                        config.getReplace().getReplacement()
                );
                System.out.println("已替换构建产物关键词");
            }

            // 5. 删除临时 ZIP
            FileUtils.cleanDir(tempZip.getParent());
            System.out.println("已删除临时文件");

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("ignore");
        } catch (Exception e) {
            throw new RuntimeException("Release handling failed for id=" + id, e);
        }
    }
}
