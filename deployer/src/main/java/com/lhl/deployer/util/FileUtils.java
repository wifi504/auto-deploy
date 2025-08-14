package com.lhl.deployer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/8/14_22:24
 */
public class FileUtils {
    /**
     * 清空目录及其子文件
     */
    public static void cleanDir(Path dir) throws IOException {
        if (!Files.exists(dir)) return;

        try (Stream<Path> stream = Files.walk(dir)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("删除失败: " + path, e);
                        }
                    });
        }
    }

    /**
     * 解压 zip 文件到目标目录
     */
    public static void unzip(Path zipFile, Path destDir) throws IOException {
        cleanDir(destDir);
        Files.createDirectories(destDir);

        try (InputStream fis = Files.newInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // 去掉顶层目录
                String entryName = entry.getName();
                int idx = entryName.indexOf('/');
                if (idx != -1) {
                    entryName = entryName.substring(idx + 1);
                }
                if (entryName.isEmpty()) continue;

                Path outPath = destDir.resolve(entryName);
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zis, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 下载远程文件到本地
     */
    public static void downloadFile(String url, Path dest) throws IOException {
        try (InputStream in = URI.create(url).toURL().openStream()) {
            Files.createDirectories(dest.getParent());
            Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 遍历目录下所有 .js | .css | .html 文件，进行字符串替换
     */
    public static void replaceInJsFiles(Path dir, String target, String replacement) throws IOException {
        if (!Files.exists(dir)) return;

        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(p -> p.toString().endsWith(".js")
                            || p.toString().endsWith(".css")
                            || p.toString().endsWith(".html"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path, StandardCharsets.UTF_8);
                            content = content.replace(target, replacement);
                            Files.writeString(path, content, StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            throw new RuntimeException("替换失败: " + path, e);
                        }
                    });
        }
    }
}