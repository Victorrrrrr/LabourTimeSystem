package com.example.labourtimesystem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labourtimesystem.common.BaseActivity;
import com.example.labourtimesystem.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TeacherLoginActivity extends BaseActivity implements View.OnClickListener,View.OnTouchListener {

    private Button btnTchLogin;
    private EditText etInputTchPwd;
    private EditText etInputTchId;
    private ImageView ivDisplayTchPwd;
    private TextView tvForgetTchPwd;
    private Retrofit retrofit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        hideBar();
        initView();

    }

    private void hideBar(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

    private void initView() {
        etInputTchPwd = findViewById(R.id.input_tch_pwd);
        etInputTchId = findViewById(R.id.input_tch_id);
        ivDisplayTchPwd = findViewById(R.id.iv_pwd_tch_see);
        tvForgetTchPwd = findViewById(R.id.tv_forget_tch_pwd);
        btnTchLogin = findViewById(R.id.btn_tch_login);

        btnTchLogin.setOnClickListener(this);
        tvForgetTchPwd.setOnClickListener(this);
        ivDisplayTchPwd.setOnTouchListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_forget_tch_pwd:
                Intent intentForgetPwd = new Intent(TeacherLoginActivity.this, ForgetPwdActivity.class);
                startActivity(intentForgetPwd);
                break;
            case R.id.btn_tch_login:
                String number = etInputTchId.getText().toString();
                String password = etInputTchPwd.getText().toString();
                tchLogin(number, password);
                break;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            etInputTchPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());  //设置为可见
            ivDisplayTchPwd.setImageResource(R.drawable.hide_pwd);
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            etInputTchPwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置密码为不可见。
            ivDisplayTchPwd.setImageResource(R.drawable.display_pwd);
            // 光标位置移到最后
            Editable editable = etInputTchPwd.getText();
            Selection.setSelection(editable, editable.length());
        }
        return true;
    }

    private void tchLogin(String number, String password){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://ldxs.wzf666.top/")
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

        Call<ResponseBody> call = helper.postTchLogin(jsonBody);
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
                String level  = dataObject.getString("level");

                Log.i("TAG", "code is " + code);
                Log.i("TAG", "major is " + major);
                Log.i("TAG", "id is " + id);
                Log.i("TAG", "token is " + token);
                Log.i("TAG", "username is " + username);
                Log.i("TAG", "msg is " + msg);

                // 记住密码？
                // @TODO: token 存入SP
                SPUtils.put(this, "token", token);
                SPUtils.put(this,"username",username);
                SPUtils.put(this, "level",level);

                Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();

                // 跳转主页
                Intent intent = null;
                if(username.equals("一级权限测试")){
                    intent = new Intent(this, TeacherLvOneMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(username.equals("二级权限测试")){
                    intent = new Intent(this, TeacherLvTwoMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    intent = new Intent(this, TeacherLvThreeMainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } else{
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}