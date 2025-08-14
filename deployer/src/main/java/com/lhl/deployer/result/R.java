package com.lhl.deployer.result;

import lombok.Builder;
import lombok.Data;

/**
 * 统一响应类：R 对象
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/3/27_0:16
 */
@Data
@Builder
public class R<T> {
    private Integer code;
    private String msg;
    private T data;


    /**
     * 成功响应（带数据）
     *
     * @param data 数据
     * @param <T>  bean 类型
     * @return 响应体
     */
    public static <T> R<T> ok(T data) {
        return R.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .msg(ResultCode.SUCCESS.getMsg())
                .data(data)
                .build();
    }

    /**
     * 成功响应（带数据和消息）
     *
     * @param data      数据
     * @param <T>       bean 类型
     * @param customMsg 自定义消息
     * @return 响应体
     */
    public static <T> R<T> ok(T data, String customMsg) {
        return R.<T>builder()
                .code(ResultCode.SUCCESS.getCode())
                .msg(customMsg)
                .data(data)
                .build();
    }

    /**
     * 错误响应
     *
     * @param codeEnum 状态码
     * @return 响应体
     */
    public static R<?> error(ResultCode codeEnum) {
        return R.builder()
                .code(codeEnum.getCode())
                .msg(codeEnum.getMsg())
                .build();
    }

    /**
     * 错误响应（附带自定义信息）
     *
     * @param codeEnum  状态码
     * @param customMsg 自定义信息
     * @return 响应体
     */
    public static R<?> error(ResultCode codeEnum, String customMsg) {
        return R.builder()
                .code(codeEnum.getCode())
                .msg("【" + codeEnum.getMsg() + "】" + customMsg)
                .build();
    }
}
