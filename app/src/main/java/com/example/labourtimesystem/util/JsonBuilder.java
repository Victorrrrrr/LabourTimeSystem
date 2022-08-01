package com.example.labourtimesystem.util;

import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/22 18:51
 * @Description : Json打包工具，快速构建Json和泛型实现
 */

public class JsonBuilder {
    private JSONObject jsonObject = new JSONObject();

    public <T> JsonBuilder addParams(String key, T value) {
        try{
            if(!(value instanceof List)){
                jsonObject.put(key, value);
            }else {
                JSONArray jsonArray = new JSONArray();

                List<Object> tempList = (List<Object>) value;
                for(Object object : tempList){
                    jsonArray.put(object);
                }
                jsonObject.put(key, jsonArray);
            }
        }catch (JsonIOException | JSONException e){
            e.printStackTrace();
        }
        return this;
    }

    public String build(){
        return jsonObject.toString();
    }

}
