package com.example.labourtimesystem.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.labourtimesystem.HttpBinHelper;
import com.example.labourtimesystem.R;
import com.example.labourtimesystem.adapter.ItemLabourRecyclerViewAdapter;
import com.example.labourtimesystem.bean.LabourItem;
import com.example.labourtimesystem.databinding.FragmentStuPreviewBinding;
import com.example.labourtimesystem.decoration.SpaceItemDecoration;
import com.example.labourtimesystem.util.HttpUtils;
import com.example.labourtimesystem.util.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StuPreviewFragment extends Fragment {
    private ArrayList<LabourItem> items = new ArrayList<>();
    private ItemLabourRecyclerViewAdapter adapter;
    private FragmentStuPreviewBinding binding;

    private final String BASE_URL = "https://ldxs.wzf666.top/";
    private CountDownLatch countdown = new CountDownLatch(1);
    private Context mContext;

    public StuPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentStuPreviewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mContext = getActivity();

        binding.previewList.setLayoutManager(new LinearLayoutManager(mContext));
        binding.previewList.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_space)));
        adapter = new ItemLabourRecyclerViewAdapter(items, mContext);
        initSwipeRefreshLayout(view);
        adapter.setOnItemClickListener((view1, position, items) -> {
            int labourId = items.get(position).getId();
            Log.i("TAG", "onItemClick: " + labourId);
            // 切换到fragment
            LabourDetailFragment fragment = new LabourDetailFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(
                            R.anim.fg_slide_in,  // enter
                            R.anim.fg_fade_out,  // exit
                            R.anim.fg_fade_in,   // popEnter
                            R.anim.fg_slide_out  // popExit
                    )
                    .replace(R.id.fragment_container_preview, fragment)
                    .addToBackStack("currentDetail")
                    .commit();

            // 传值items.get(position).getId()
            Bundle bundle = new Bundle();
            bundle.putInt("labourId", labourId);
            getParentFragmentManager().setFragmentResult("resultKey", bundle);
        });
        binding.previewList.setAdapter(adapter);
        binding.previewList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.e("TAG", "onScrolled: " + isSlideToBottom(recyclerView));

            }
        });

        return view;
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset()
                >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    private void initSwipeRefreshLayout(View view) {
        binding.swipePreviewStu.setColorSchemeColors(getResources().getColor(R.color.right_bottom));
        binding.swipePreviewStu.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        binding.swipePreviewStu.setRefreshing(true);
        binding.swipePreviewStu.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            sendRequest(mContext);
            adapter.setItemLabourList(items);
            adapter.notifyDataSetChanged();
            binding.swipePreviewStu.setRefreshing(false);
        },800));
        if(binding.swipePreviewStu.isRefreshing()){
            sendRequest(mContext);
            new Thread(() -> {
                // 等待后台返回数据
                try {
                    countdown.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(() -> {
                    adapter.setItemLabourList(items);
                    adapter.notifyDataSetChanged();
                    binding.swipePreviewStu.setRefreshing(false);
//                    if(hasRecyclerViewData(adapter)){
//
//                    }
                });
            }).start();
        }
    }


    private boolean hasRecyclerViewData(ItemLabourRecyclerViewAdapter adapter){
        if(adapter.getItemCount() == 0)
            return false;
        else
            return true;
    }

    private void sendRequest(Context mContext) {
        Retrofit retrofit = HttpUtils.initRetrofit(mContext);
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

        String token = (String) SPUtils.get(mContext,"token", "");
        Call<ResponseBody> call = helper.getActivityList(token, requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(null != response && response.isSuccessful()) {
                    try {
                        String result = response.body().string();
                        parseJsonObject(result);
                        Log.i("TAG", "onResponse: " + items.size());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        // 告知结束等待
                        countdown.countDown();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                // 告知结束等待
                countdown.countDown();
            }
        });
    }

    private void parseJsonObject(String jsonData) {
        String[] statusArray = new String[]{"已结束", "已开始", "未开始", "未开始", "未开始", "活动预告"};
        String[] typeArray = new String[]{"","日常性劳动", "创造性劳动", "服务性劳动", "其他劳动"};

        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");

            if(code.equals("0")){
                String data = jsonObject.getString("data");
                JSONObject dataObject = new JSONObject(data);
                int total = dataObject.getInt("total");
                JSONArray labors = dataObject.getJSONArray("labors");
                for(int i = 0; i < labors.length(); i++){
                    JSONObject labor = labors.getJSONObject(i);
                    String beginTime = labor.getString("beginTime");
                    String[] dateString = beginTime.split(" ");
                    String time = dateString[1].substring(0,5);
                    int capacity = labor.getInt("capacity");
                    String date = labor.getString("date");
                    String department = labor.getString("department");
                    int id = labor.getInt("id");
                    int isTop = labor.getInt("isTop");
                    String name = labor.getString("name");
                    String photo = labor.getString("photo");
                    String position = labor.getString("position");
                    int status = labor.getInt("status");
                    String statusStr = statusArray[status + 1];
                    int type = labor.getInt("type");
                    String typeStr = typeArray[type];

                    LabourItem labourItem = new LabourItem(id, name, date, time, capacity,
                            typeStr, position, department, statusStr);
                    // Log.i("TAG", "parseJsonObject: " + labourItem.toString());
                    items.add(labourItem);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}