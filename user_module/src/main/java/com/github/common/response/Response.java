package com.github.common.response;

import lombok.Getter;

@Getter
public class Response<T> {
    private String message;
    private String code;
    private T data;

    public Response(final ResponseCode responseCode, final T data) {
        this.message = responseCode.getMessage();
        this.code = responseCode.getCode();
        this.data = data;
    }

    public static <T> Response<T> success(final ResponseCode responseCode, final T data) {
        return new Response<>(responseCode, data);
    }

    public static <T> Response<T> success(final T data) {
        return new Response<>(ResponseCode.SUCCESS, data);
    }

    public static <T> Response<T> success() {
        return new Response<>(ResponseCode.SUCCESS, null);
    }

    public static <T> Response<T> error(final ResponseCode responseCode, final T  data) {
        return new Response<>(responseCode, data);
    }

    public static <T> Response<T> error(final T  data) {
        return new Response<>(ResponseCode.FAILURE, data);
    }
}