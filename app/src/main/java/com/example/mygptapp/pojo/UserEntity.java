package com.example.mygptapp.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserEntity {
    @PrimaryKey
    private Integer userID;
    private String userName;
    private String password;
    private String mobilePhone;
    private String defaultModel;
    private String defaultPrompt;
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }

    public String getDefaultPrompt() {
        return defaultPrompt;
    }

    public void setDefaultPrompt(String defaultPrompt) {
        this.defaultPrompt = defaultPrompt;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "userID=" + userID +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", defaultModel='" + defaultModel + '\'' +
                ", defaultPrompt='" + defaultPrompt + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
