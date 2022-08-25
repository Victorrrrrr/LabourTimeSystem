package com.example.labourtimesystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.labourtimesystem.adapter.DropListViewAdapter;
import com.example.labourtimesystem.adapter.StudentRgAdapter;
import com.example.labourtimesystem.bean.Bean;
import com.example.labourtimesystem.common.BaseActivity;
import com.example.labourtimesystem.fragment.StuCurrentFragment;
import com.example.labourtimesystem.fragment.StuMyFragment;
import com.example.labourtimesystem.fragment.StuPreviewFragment;
import com.example.labourtimesystem.util.DateUtils;
import com.example.labourtimesystem.util.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class StudentMainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{

    private ViewPager2 vpStu;

    private StuCurrentFragment stuCurrentFragment;
    private StuPreviewFragment stuPreviewFragment;
    private StuMyFragment stuMyFragment;
    private int startX = 0;
    private int startY = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);
        StudentRgAdapter adapter = new StudentRgAdapter(this);

        RadioGroup rg = findViewById(R.id.rg_stu);
        rg.setOnCheckedChangeListener(this);
        vpStu = findViewById(R.id.vp_stu);

        vpStu.setAdapter(adapter);
        if(stuCurrentFragment == null){
            stuCurrentFragment = new StuCurrentFragment();
        }
        if(stuPreviewFragment == null){
            stuPreviewFragment = new StuPreviewFragment();
        }
        if(stuMyFragment == null){
            stuMyFragment = new StuMyFragment();
        }
        adapter.addFragment(stuPreviewFragment);
        adapter.addFragment(stuCurrentFragment);
        adapter.addFragment(stuMyFragment);

        vpStu.setCurrentItem(1);

        vpStu.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        ((RadioButton)findViewById(R.id.rb_preview)).setChecked(true);
                        break;
                    case 1:
                        ((RadioButton)findViewById(R.id.rb_current)).setChecked(true);
                        break;
                    case 2:
                        ((RadioButton)findViewById(R.id.rb_my)).setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        });

    }

    // RecyclerView滑动时ViewPager2滑动过于灵敏问题
    // 对dispatchTouchEvent方法进行重写
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = (int)ev.getX();
                startY = (int)ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int endX = (int)ev.getX();
                int endY = (int)ev.getY();
                int disX = Math.abs(endX - startX);
                int disY = Math.abs(endY - startY);
                if (disX < disY){
                    vpStu.setUserInputEnabled(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                startX = 0;
                startY = 0;
                vpStu.setUserInputEnabled(true);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_preview:
                vpStu.setCurrentItem(0);
                break;
            case R.id.rb_current:
                vpStu.setCurrentItem(1);
                break;
            case R.id.rb_my:
                vpStu.setCurrentItem(2);
                break;
        }

    }


}