package com.example.mygptapp.pojo;

public class Message {
    private String system;
    private String content;
    private String type;

    public Message(String system, String content, String type) {
        this.system = system;
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setType(String type) {
        this.type = type;
    }
}
