package com.tamp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel {

    public ResponseModel statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseModel data(Object data) {
        this.data = data;
        return this;
    }

    public ResponseModel message(String message) {
        this.message = message;
        return this;
    }

    private int statusCode;

    private Object data;

    private String message;
}
