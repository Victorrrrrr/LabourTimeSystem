package com.example.labourtimesystem.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/4/25 0:37
 * @Description : Fragments和 RadioGroup的适配器
 */
public class StudentRgAdapter extends FragmentStateAdapter {

    public List<Class> fragments;

    public StudentRgAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        if(fragments == null){
            fragments = new ArrayList<>();
        }
    }

    public void addFragment(Fragment fragment){
        if(fragments != null){
            fragments.add(fragment.getClass());
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try{
            return (Fragment)fragments.get(position).newInstance();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (InstantiationException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
