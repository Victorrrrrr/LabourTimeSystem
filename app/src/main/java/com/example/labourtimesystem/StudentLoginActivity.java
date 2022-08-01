package com.example.labourtimesystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labourtimesystem.common.BaseActivity;
import com.example.labourtimesystem.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentLoginActivity extends BaseActivity implements View.OnTouchListener,View.OnClickListener{

    private Button btnStuLogin;
    private EditText etInputStuPwd;
    private ImageView ivDisplayStuPwd;
    private TextView tvForgetStuPwd;
    private EditText etInputStuId;
    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        hideBar();
        initView();
        

    }

    private void initView() {
        etInputStuPwd = findViewById(R.id.input_stu_pwd);
        ivDisplayStuPwd = findViewById(R.id.iv_pwd_stu_see);
        tvForgetStuPwd = findViewById(R.id.tv_forget_stu_pwd);
        etInputStuId = findViewById(R.id.input_stu_id);
        btnStuLogin = findViewById(R.id.btn_stu_login);

        btnStuLogin.setOnClickListener(this);
        tvForgetStuPwd.setOnClickListener(this);
        ivDisplayStuPwd.setOnTouchListener(this);

    }



    private void hideBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_forget_stu_pwd:
                Intent intent = new Intent(StudentLoginActivity.this, ForgetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_stu_login:
                String number = etInputStuId.getText().toString();
                String password = etInputStuPwd.getText().toString();
                stuLogin(number, password);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            etInputStuPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  //设置为可见
            ivDisplayStuPwd.setImageResource(R.drawable.hide_pwd);  // 更改图标
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            etInputStuPwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置密码为不可见。
            ivDisplayStuPwd.setImageResource(R.drawable.display_pwd);   // 更改图标
            // 光标位置移到最后
            Editable editable = etInputStuPwd.getText();
            Selection.setSelection(editable, editable.length());
        }
        return true;
    }


    private void stuLogin(String number, String password){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://ldxs.wzf666.top/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);

        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try{
            data.put("number",number);
            data.put("password",password);
            data.put("code","xxx");
            jsonObject.put("data",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String json = String.valueOf(jsonObject);
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
        RequestBody jsonBody = RequestBody.create(jsonType, json);

        Call<ResponseBody> call = helper.postStuLogin(jsonBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    Log.i("TAG", "onResponse: " + result);
                    parseJsonObject(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // 解析Json格式
    private void parseJsonObject(String jsonData){
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");

            if(code.equals("0")){
                String data = jsonObject.getString("data");
                JSONObject dataObject = new JSONObject(data);
                int id = dataObject.getInt("id");
                String major = dataObject.getString("major");
                String username = dataObject.getString("username");
                String token = dataObject.getString("token");
                String number = dataObject.getString("number");
                String year = dataObject.getString("year"); // 该学生的当前年级，用于计算是否转专业

                // @TODO: token 存入SP
                SPUtils.put(this, "token", token);
                SPUtils.put(this,"username",username);
                SPUtils.put(this,"major",major);
                SPUtils.put(this,"year",year);
                SPUtils.put(this,"number",number);
                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();

                // 单例模式创建UserHelp类，保存用户信息
                UserHelp userHelp = UserHelp.getInstance();
                userHelp.setUsername(username);


                // 跳转主页
                Intent intent = new Intent(StudentLoginActivity.this, StudentMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 关闭前面的所有Activity
                startActivity(intent);
            }else{
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}