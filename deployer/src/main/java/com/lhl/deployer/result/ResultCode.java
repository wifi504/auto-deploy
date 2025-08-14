package com.lhl.deployer.result;

import lombok.Getter;

/**
 * 状态码枚举类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/3/27_0:22
 */

@Getter
public enum ResultCode {

    SUCCESS(200, "请求成功"),
    FAIL(400, "请求错误"),
    UNAUTHORIZED(401, "无效认证"),
    FORBIDDEN(403, "拒绝访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    SERVER_ERROR(500, "服务器内部错误");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
