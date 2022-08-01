package com.example.labourtimesystem.bean;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/30 16:33
 * @Description : 描述
 */
public class MyActivity {
    private String beginTime;
    private int capacity;
    private String department;
    private int id;
    private String latitude;
    private String longitude;
    private String name;
    private String position;
    private int state;
    private int status;
    private int type;

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }
    public String getBeginTime() {
        return beginTime;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public int getCapacity() {
        return capacity;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
    public String getDepartment() {
        return department;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getPosition() {
        return position;
    }

    public void setState(int state) {
        this.state = state;
    }
    public int getState() {
        return state;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "MyActivity{" +
                "beginTime='" + beginTime + '\'' +
                ", capacity=" + capacity +
                ", department='" + department + '\'' +
                ", id=" + id +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", state=" + state +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}