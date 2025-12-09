package com.unimelbproject.healthcareequipmentassistant.interfaces;

public class Response<T> implements IResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public Response() {}

    public Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(true, "Success", data);
    }

    public static <T> Response<T> failure(String message) {
        return new Response<>(false, message, null);
    }

    public static <T> Response<T> failure(String message, T data) {
        return new Response<>(false, message, data);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public T getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
}
