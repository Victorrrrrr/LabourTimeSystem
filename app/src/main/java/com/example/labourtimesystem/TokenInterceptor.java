package com.example.labourtimesystem;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.labourtimesystem.util.SPUtils;
import com.example.labourtimesystem.view.OfflineDialog;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/4/21 20:52
 * @Description : Token 拦截器
 */
public class TokenInterceptor implements Interceptor {
    private Context mContext;
    private OfflineDialog offlineDialog;

    public TokenInterceptor(Context context){
        mContext = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        ResponseBody responseBody = response.body();
        if(responseBody != null){
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            try {
                String result =buffer.clone().readString(StandardCharsets.UTF_8);
                JSONObject jsonObject = new JSONObject(result);
                int code = jsonObject.getInt("code");
                // 如果token过期，清空SP保存的token，并跳转到登录选择页面，进行重新登录
                if(code == 401){
                    // 清空token
                    SPUtils.put(mContext,"token","");
                    // 跳转页面
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("reLogin","强制下线：账号已在别的设备登录");
                    mContext.startActivity(intent);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return response;
    }

}
