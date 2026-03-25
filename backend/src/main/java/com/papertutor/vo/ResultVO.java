package com.papertutor.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一响应结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVO<T> {

    private Integer code;
    private String message;
    private T data;

    /**
     * 成功响应
     */
    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(200, "success", data);
    }

    /**
     * 成功响应（带消息）
     */
    public static <T> ResultVO<T> success(String message, T data) {
        return new ResultVO<>(200, message, data);
    }

    /**
     * 失败响应
     */
    public static <T> ResultVO<T> error(Integer code, String message) {
        return new ResultVO<>(code, message, null);
    }

    /**
     * 失败响应（默认错误码）
     */
    public static <T> ResultVO<T> error(String message) {
        return new ResultVO<>(500, message, null);
    }
}
