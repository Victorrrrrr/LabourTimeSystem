package com.example.labourtimesystem.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.labourtimesystem.HttpBinHelper;
import com.example.labourtimesystem.R;
import com.example.labourtimesystem.bean.LabourDetailData;
import com.example.labourtimesystem.databinding.FragmentLabourDetailBinding;
import com.example.labourtimesystem.util.HttpUtils;
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

public class LabourDetailFragment extends Fragment{
    private FragmentLabourDetailBinding binding;

    public LabourDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide_right));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLabourDetailBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Context mContext = getActivity();
        setRoundImg(view,mContext);

        getParentFragmentManager().setFragmentResultListener("resultKey",
                this, (requestKey, result) -> {
                    Log.i("TAG", "value: " + result.getInt("labourId"));
                    int labourId = result.getInt("labourId");
                    SPUtils.put(mContext, "labourId", labourId);
                    getDetailRequest(labourId, mContext);
                });
        int labourId = (Integer) SPUtils.get(mContext, "labourId", 0);
        binding.btnSign.setOnClickListener(v -> clickSign(labourId, mContext));
        binding.ibReturn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void clickSign(int labourId, Context mContext) {
        Log.e("TAG", "clickSign: " + labourId);
        Retrofit retrofit = HttpUtils.initRetrofit(mContext);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("laborId", labourId);
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json charset=utf-8"), String.valueOf(jsonObject));
        String token =(String)SPUtils.get(mContext, "token", "");
        Call<ResponseBody> call = helper.signUp(token, requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String result = response.body().string();
                    JSONObject object = new JSONObject(result);
                    String msg = object.getString("msg");
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getDetailRequest(int labourId, Context context) {
        String[] statusArray = new String[]{"已结束", "已开始", "未开始", "未开始", "未开始", "活动预告"};
        String[] typeArray = new String[]{"","日常性劳动", "创造性劳动", "服务性劳动", "其他劳动"};
        Retrofit retrofit = HttpUtils.initRetrofit(context);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);
        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("laborId", labourId);
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8;"), String.valueOf(jsonObject));
        String token =(String)SPUtils.get(context, "token", "");
        Call<LabourDetailData> call = helper.getDetail(token, requestBody);
        call.enqueue(new Callback<LabourDetailData>() {
            @Override
            public void onResponse(Call<LabourDetailData> call, Response<LabourDetailData> response) {
                if(response.body().getCode() == 0){
                    binding.tvDetailName.setText(response.body().getData().getName());
                    binding.tvStatus.setText(statusArray[response.body().getData().getStatus()+1]);
                    binding.tvDescription.setText(response.body().getData().getDescription());
                    binding.closeTime.setText(response.body().getData().getEndTime().substring(11,19));
                    binding.tvOpenTime.setText(response.body().getData().getBeginTime());
                    binding.tvPlace.setText(response.body().getData().getPosition());
                    binding.tvType.setText(typeArray[response.body().getData().getType()]);
                    binding.tvDepartment.setText(response.body().getData().getDepartment());
                }else {
                    Toast.makeText(context , response.body().getMsg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LabourDetailData> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setRoundImg(View view, Context context) {
        // Glide 设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(100);
        // 通过RequestOptions拓展功能，override；ImageView大小一定，所以可以压缩图片，降低内存消耗
        ImageView imageView = view.findViewById(R.id.labour_detail_img);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context)
                .load(R.drawable.detail_bg)
                .transform(roundedCorners)
                .into(imageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}