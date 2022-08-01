package com.example.labourtimesystem;

import android.content.Context;
import android.util.Log;

import com.example.labourtimesystem.util.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String BASE_URL = "https://ldxs.wzf666.top/";
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new TokenInterceptor(mContext))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .build();

        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);

        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put("currentPage", 1);
            data.put("pageSize", 20);
            data.put("term","202120222");
            data.put("keyWord","");
            data.put("status",4);
            data.put("type", 0);
            jsonObject.put("data", data);
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String json = String.valueOf(jsonObject);
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, json);
        System.out.println(json);
//        String token = (String) SPUtils.get(mContext,"token", "");
        Call<ResponseBody> call = helper.getActivityList("88b3e0d5a3bd45de93a71465f7aaf4c6", requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    System.out.println(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("1111");
                t.printStackTrace();
            }
        });


    }
}