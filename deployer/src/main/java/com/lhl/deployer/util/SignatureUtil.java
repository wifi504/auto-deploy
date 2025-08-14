package com.lhl.deployer.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;

/**
 * 签名验证
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/8/14_22:24
 */
public class SignatureUtil {
    /**
     * 验证签名
     *
     * @param payload         请求体
     * @param signatureHeader 请求头
     * @param secret          密钥
     * @return boolean 验证结果
     */
    public static boolean verify(byte[] payload, String signatureHeader, String secret) {
        try {
            if (!signatureHeader.startsWith("sha256=")) {
                return false;
            }
            String sig = signatureHeader.substring(7);

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] hmac = mac.doFinal(payload);
            String expected = HexFormat.of().formatHex(hmac);
            return expected.equals(sig);
        } catch (Exception e) {
            return false;
        }
    }
}
