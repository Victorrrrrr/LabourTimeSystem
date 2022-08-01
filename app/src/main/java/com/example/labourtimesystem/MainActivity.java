package com.example.labourtimesystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.labourtimesystem.util.SPUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private View llStu;
    private View llTch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideBar();

        llStu = findViewById(R.id.stu_login);
        llTch = findViewById(R.id.tch_login);
        llTch.setOnClickListener(this);
        llStu.setOnClickListener(this);

        reLoginMsg();

    }

    private void hideBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void reLoginMsg(){
        Intent intent = getIntent();
        String reLoginMsg = intent.getStringExtra("reLogin");
        if(reLoginMsg != null){
            Toast.makeText(this, reLoginMsg, Toast.LENGTH_SHORT).show();
            Log.i("TAG", "msg: " + reLoginMsg);
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.stu_login:
                intent = new Intent(MainActivity.this, StudentLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tch_login:
                intent = new Intent(MainActivity.this, TeacherLoginActivity.class);
                startActivity(intent);
                break;
        }
    }

}