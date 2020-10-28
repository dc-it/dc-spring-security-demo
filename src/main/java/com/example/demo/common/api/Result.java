package com.example.demo.common.api;

import lombok.Data;

/**
 * 通用返回对象
 *
 * @author duchao
 */
@Data
public class Result<T> {

    private int code;

    private String message;

    private T data;

    protected Result() {
    }

    protected Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(0, "成功", data);
    }

    /**
     * 失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failure(String message) {
        return new Result<T>(-1, message, null);
    }
}
