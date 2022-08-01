package com.example.labourtimesystem.bean;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/3 16:59
 * @Description : 描述
 */
public class Bean {

    String termTime;
    int status;
    int type;

    public Bean(){}

    public Bean(String termTime, int status, int type) {
        this.termTime = termTime;
        this.status = status;
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTermTime() {
        return termTime;
    }

    public void setTermTime(String termTime) {
        this.termTime = termTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
