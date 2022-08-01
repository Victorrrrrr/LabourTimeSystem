package com.example.labourtimesystem.common;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/7/28 13:47
 * @Description : 描述
 */
public class ActivityController {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for(Activity activity : activities){
            if(!activity.isFinishing()) {
                activity.finish();
            }
        }
    }


}
