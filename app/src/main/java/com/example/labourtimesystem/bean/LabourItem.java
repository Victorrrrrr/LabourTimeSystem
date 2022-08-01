package com.example.labourtimesystem.bean;

import android.content.Context;

import com.example.labourtimesystem.TokenInterceptor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 */


public class LabourItem {

    public int id;  // 活动id
    public String title;   // 活动标题
    public String date;    // 活动日期
    public String time;    // 活动时间
    public int number;     // 活动参与人数
    public String type;    // 活动类型
    public String place;   // 活动地点
    public String department;  // 活动举办学院
    public String status;      // 活动状态

    public LabourItem(){}

    public LabourItem(int id, String title, String date,
                      String time, int number, String type,
                      String place, String department, String status) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.number = number;
        this.type = type;
        this.place = place;
        this.department = department;
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getNumber() {
        return number;
    }

    public String getType() {
        return type;
    }

    public String getPlace() {
        return place;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() {
        return status;
    }


    @Override
    public @NotNull String toString() {
        return id + ' ' + title + ' ' + date + ' ' + time + ' ' + number
                + ' ' + type + ' ' + place + ' ' + department + ' ' + status;
    }
}
