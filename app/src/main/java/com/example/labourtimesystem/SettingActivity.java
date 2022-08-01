package com.example.labourtimesystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labourtimesystem.bean.CommonResponse;
import com.example.labourtimesystem.common.BaseActivity;
import com.example.labourtimesystem.databinding.ActivitySettingBinding;
import com.example.labourtimesystem.util.HttpUtils;
import com.example.labourtimesystem.util.JsonBuilder;
import com.example.labourtimesystem.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnLogout.setOnClickListener(this);
        binding.llChangPwd.setOnClickListener(this);
        binding.llBindEmail.setOnClickListener(this);
        binding.llProblem.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                SPUtils.clear(getApplicationContext());
                Intent intent = new Intent();
                intent.setClass(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 关闭前面的所有Activity
                Toast.makeText(this, "退出成功", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                break;
            case R.id.ll_chang_pwd:
                initPwdDialog();
                break;
            case R.id.ll_bind_email:
                initEmailDialog();
                break;
            case R.id.ll_problem:
                initProblemDeal();
                break;

            default:
                break;
        }
    }

    private void initProblemDeal() {
        Toast.makeText(this, "暂未开发", Toast.LENGTH_SHORT).show();
    }

    private void initEmailDialog() {
        Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.view_bind_email_dialog, null);

        mDialog.setContentView(dialogView);
        mDialog.setCancelable(false);
        mDialog.show();

        dialogView.findViewById(R.id.ib_dialog_email_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.dialog_btn_send_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) dialogView.findViewById(R.id.dialog_et_email)).getText().toString();
                if(!email.equals("")){
                    getEmailCodeRequest(email);
                }else {
                    ((TextView)dialogView.findViewById(R.id.tv_dialog_tip)).setText("邮箱为空，请输入");
                }
            }
        });

        dialogView.findViewById(R.id.btn_dialog_email_certain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) dialogView.findViewById(R.id.dialog_et_email)).getText().toString();
                String code =((EditText)dialogView.findViewById(R.id.dialog_et_code)).getText().toString();
                if(!email.equals("") && !code.equals("")){
                    bingEmailRequest(code, mDialog);
                }else if(code.equals("")){
                    ((TextView)dialogView.findViewById(R.id.tv_dialog_tip)).setText("验证码为空，请输入");
                }else {
                    ((TextView)dialogView.findViewById(R.id.tv_dialog_tip)).setText("邮箱为空，请输入");
                }
            }
        });


    }

    private void getEmailCodeRequest(String email) {
        Retrofit retrofit = HttpUtils.initRetrofit(this);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);
        String token = (String) SPUtils.get(this, "token", "");
        JSONObject data = new JSONObject();
        try {
            data.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = new JsonBuilder().addParams("data",data).build();
        MediaType type = MediaType.parse("application/json charset=utf-8;");
        RequestBody body = RequestBody.create(type, json);
        Call<CommonResponse> call = helper.bindEmailCode(token,body);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Toast.makeText(SettingActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void bingEmailRequest(String code, Dialog dialog) {
        Retrofit retrofit = HttpUtils.initRetrofit(this);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);
        String token = (String) SPUtils.get(this, "token", "");
        JSONObject data = new JSONObject();
        try {
            data.put("verificationCode", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = new JsonBuilder().addParams("data",data).build();
        MediaType type = MediaType.parse("application/json charset=utf-8;");
        RequestBody body = RequestBody.create(type, json);
        Call<CommonResponse> call = helper.bindEmail(token,body);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if(response.body().getCode() == 0){
                    dialog.dismiss();
                }
                Toast.makeText(SettingActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    private void initPwdDialog() {
        Dialog mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.view_reset_pwd_dialog, null);

        mDialog.setContentView(dialogView);
        mDialog.setCancelable(false);
        mDialog.show();

        dialogView.findViewById(R.id.ib_dialog_pwd_cancel).setOnClickListener(v1 -> {
            mDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_dialog_pwd_certain).setOnClickListener(v12 -> {
            String oldPwd = ((EditText)dialogView.findViewById(R.id.dialog_et_email)).getText().toString();
            String newPwd = ((EditText)dialogView.findViewById(R.id.dialog_btn_send_code)).getText().toString();
            String newPwdAgain = ((EditText)dialogView.findViewById(R.id.dialog_et_code)).getText().toString();

            // 判空
            if(!oldPwd.equals("") && !newPwd.equals("") && !newPwdAgain.equals("")) {
                // 判断两次密码是否一致
                if (newPwd.equals(newPwdAgain)) {
                    if(newPwd.length() >= 6 && newPwdAgain.length() >= 6) {
                        editPwdRequest(oldPwd, newPwd, mDialog, dialogView);
                    } else {
                        ((TextView) dialogView.findViewById(R.id.tv_dialog_tip)).setText("密码需要6位或6位以上");
                    }
                } else {
                    // 设置警告
                    ((TextView) dialogView.findViewById(R.id.tv_dialog_tip)).setText("两次密码不一致");
                    // 输入框变色
                    ((EditText) dialogView.findViewById(R.id.dialog_et_code))
                            .setBackground(getResources()
                                    .getDrawable(R.drawable.shape_dialog_input_alert));
                }
            }else{
                // 设置警告
                ((TextView) dialogView.findViewById(R.id.tv_dialog_tip)).setText("请填写完整信息");
            }
        });
    }

    private void editPwdRequest(String oldPwd, String newPwd, Dialog mDialog, View dialogView) {
        Retrofit retrofit = HttpUtils.initRetrofit(this);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);
        String token = (String) SPUtils.get(this, "token", "");
        JSONObject data = new JSONObject();
        try {
            data.put("oldPassword",oldPwd);
            data.put("newPassword",newPwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String json = new JsonBuilder().addParams("data",data).build();
        Log.i("code", "editPwdRequest: " + json);
        MediaType type = MediaType.parse("application/json charset=utf-8;");
        RequestBody body = RequestBody.create(type, json);
        Call<CommonResponse> call = helper.editPwd(token,body);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Log.e("code", "onResponse: " + response.body().getCode() + "  " + response.body().getMsg() );
                if(response.body().getCode() == 0){
                    Toast.makeText(SettingActivity.this, response.body().getMsg(), Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                    // 如何回收Dialog？
                }else {
                    ((TextView)dialogView.findViewById(R.id.tv_dialog_tip)).setText(response.body().getMsg());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

}
