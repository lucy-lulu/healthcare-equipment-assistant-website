package com.unimelbproject.healthcareequipmentassistant.interfaces;

public interface IResponse<T> {
    boolean isSuccess();
    String getMessage();
    T getData();
}
