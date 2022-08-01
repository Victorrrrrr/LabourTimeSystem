package com.example.labourtimesystem.util;

import android.content.Context;

import com.example.labourtimesystem.TokenInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/4/22 11:05
 * @Description : 网络请求工具类
 */
public class HttpUtils {

    private static final String BASE_URL = "https://ldxs.wzf666.top/";

    public static Retrofit initRetrofit(Context context){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor(context))
                .connectTimeout(20000, TimeUnit.SECONDS)
                .readTimeout(20000,TimeUnit.MILLISECONDS)
                .writeTimeout(20000,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

}
