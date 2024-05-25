package com.example.mygptapp.pojo;

import com.google.gson.JsonElement;

import java.util.List;

public class MyResponse {
    private String status;
    private String message;
    private JsonElement data;

    public MyResponse(){
    }

    public MyResponse(String error, String message) {
        this.status = error;
        this.message = message;
        this.data = null;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(JsonElement data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }
}
