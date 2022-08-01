package com.example.labourtimesystem.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/30 2:46
 * @Description : 描述
 */
public class MyActivities {
    private int code;
    @SerializedName("data")
    private List<MyActivity> myActivityList;
    private String msg;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMyActivityList(List<MyActivity> myActivityList) {
        this.myActivityList = myActivityList;
    }
    public List<MyActivity> getMyActivityList() {
        return myActivityList;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "MyActivities{" +
                "code=" + code +
                ", myActivityList=" + myActivityList +
                ", msg='" + msg + '\'' +
                '}';
    }
}
