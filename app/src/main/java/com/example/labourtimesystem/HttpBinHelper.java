package com.example.labourtimesystem;

import com.example.labourtimesystem.bean.MyActivities;
import com.example.labourtimesystem.bean.LabourDetailData;
import com.example.labourtimesystem.bean.CommonResponse;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/4/18 21:02
 * @Description : 描述
 */
public interface HttpBinHelper {

    @POST("/student/login")
    Call<ResponseBody> postStuLogin(@Body RequestBody json);

    @POST("/admin/login")
    Call<ResponseBody> postTchLogin(@Body RequestBody json);

    @POST("/student/labor/getLaborList")
    Call<ResponseBody> getActivityList(@Header("token") String token, @Body RequestBody json);

    @POST("/student/labor/getLaborDetail")
    Call<LabourDetailData> getDetail(@Header("token") String token, @Body RequestBody data);

    @POST("/student/labor/signUp")
    Call<ResponseBody> signUp(@Header("token") String token, @Body RequestBody json);

    @POST("/common/changePassword")
    Call<CommonResponse> editPwd(@Header("token") String token, @Body RequestBody body);

    @POST("/common/bindEmail")
    Call<CommonResponse> bindEmail(@Header("token") String token, @Body RequestBody body);

    @POST("/common/sendBindEmailCode")
    Call<CommonResponse> bindEmailCode(@Header("token") String token, @Body RequestBody body);

    @POST("/student/labor/getMyLaborList")
    Call<MyActivities> getJoinActivity(@Header("token") String token, @Body RequestBody body);

}
