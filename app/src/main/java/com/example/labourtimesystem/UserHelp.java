package com.example.labourtimesystem;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/4/24 8:15
 * @Description : 描述
 */
public class UserHelp {
    private static UserHelp instance;
    private UserHelp(){

    }

    // 双重检验锁
    public static UserHelp getInstance(){
        if(instance == null){
            synchronized (UserHelp.class){
                if (instance ==  null){
                    instance = new UserHelp();
                }
            }
        }
        return instance;
    }


    private String username;

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

}

