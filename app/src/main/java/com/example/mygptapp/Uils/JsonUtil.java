package com.example.mygptapp.Uils;

import com.example.mygptapp.pojo.MyResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonUtil {
    // 将json字符串转换为对象数组,如{"status":"error","message":"用户不存在","data":null}
    public static MyResponse parseJson(String json){
        Gson gson = new Gson();
        MyResponse res = gson.fromJson(json, MyResponse.class);
        return res;
    }
}
