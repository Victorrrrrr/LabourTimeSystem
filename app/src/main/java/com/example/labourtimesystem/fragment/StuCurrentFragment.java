package com.example.labourtimesystem.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionInflater;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labourtimesystem.HttpBinHelper;
import com.example.labourtimesystem.R;
import com.example.labourtimesystem.adapter.DropListViewAdapter;
import com.example.labourtimesystem.adapter.ItemLabourRecyclerViewAdapter;
import com.example.labourtimesystem.bean.Bean;
import com.example.labourtimesystem.bean.LabourItem;
import com.example.labourtimesystem.decoration.SpaceItemDecoration;
import com.example.labourtimesystem.util.DateUtils;
import com.example.labourtimesystem.util.HttpUtils;
import com.example.labourtimesystem.util.SPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class StuCurrentFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    // TODO:Customize item list
    private ArrayList<LabourItem> items = new ArrayList<>();
    private List<Bean> statusData = new ArrayList<>();
    private List<Bean> timeData = new ArrayList<>();

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PopupWindow popupWindow;
    private ItemLabourRecyclerViewAdapter adapter;
    private Context mContext;
    private SwipeRefreshLayout refreshCurrent;
    private RadioGroup rgTypeSelect;

    private CountDownLatch countdown;

    private final String[] statusArray = new String[]{"已结束", "已开始",
            "未开始", "未开始", "未开始", "活动预告"};
    private final String[] typeArray = new String[]{"","日常性劳动",
            "创造性劳动", "服务性劳动", "其他劳动"};
    private static Bean chooseBean = new Bean();


    public StuCurrentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 切换Fragment的过渡动画
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));

        // 设置Toolbar
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stu_current, container, false);
        mContext = getActivity();
        recyclerView = view.findViewById(R.id.current_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_space)));
        adapter = new ItemLabourRecyclerViewAdapter(items, mContext);
        initToolbar(view);
        initPopupData(view);
        initSwipeRefreshLayout(view);
        adapter.setOnItemClickListener((view1, position, items) -> {
            int labourId = items.get(position).getId();
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
                    .replace(R.id.fragment_container_current, fragment)
                    .addToBackStack("currentDetail")
                    .commit();

            // 传值items.get(position).getId()
            Bundle bundle = new Bundle();
            bundle.putInt("labourId", labourId);
            getParentFragmentManager().setFragmentResult("resultKey", bundle);
        });
        recyclerView.setAdapter(adapter);

        rgTypeSelect = view.findViewById(R.id.rg_type_select);
        rgTypeSelect.setOnCheckedChangeListener(this);

        view.findViewById(R.id.rl_status_choose).setOnClickListener(v -> statusChoose(v, mContext));
        view.findViewById(R.id.rl_term_choose).setOnClickListener(v -> timeChoose(v, mContext));
        view.findViewById(R.id.ib_reset_list).setOnClickListener(v -> resetList(mContext));

        return view;
    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_daily_labour:
                responseCheckedChanged(mContext, chooseBean, 1);
                break;
            case R.id.rb_create_labour:
                responseCheckedChanged(mContext, chooseBean, 2);
                break;
            case R.id.rb_service_labour:
                responseCheckedChanged(mContext, chooseBean, 3);
                break;
            case R.id.rb_other_labour:
                responseCheckedChanged(mContext, chooseBean, 4);
                break;
        }
    }

    public void responseCheckedChanged(Context mContext, Bean chooseBean, int type){
        chooseBean.setType(type);
        // 发送请求
        sendRequest(mContext, chooseBean);
        countdown = new CountDownLatch(1);
        // 更新列表
        new Thread(() -> {
            try {
                countdown.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getActivity().runOnUiThread(() -> {
                adapter.setItemLabourList(items);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.setItemLabourList(items);
        adapter.notifyDataSetChanged();
        Log.i("TAG", "onResume: "+ items.size());
    }

    /**
     *
     * 发送请求，获取活动列表
     * @param mContext 上下文
     * @param selectArgs  选择的Bean对象
     */
    public void sendRequest(Context mContext, Bean selectArgs){

        Retrofit retrofit = HttpUtils.initRetrofit(mContext);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);

        JSONObject jsonObject = new JSONObject();
        JSONObject data = new JSONObject();

        try {
            data.put("currentPage", 1);
            data.put("pageSize", 20);
            data.put("term",selectArgs.getTermTime());
            data.put("keyWord","");
            data.put("status",selectArgs.getStatus());
            Log.e("TAG", "sendRequest: " + selectArgs.getTermTime()+ " " + selectArgs.getType()+ " " + selectArgs.getStatus());
            data.put("type", selectArgs.getType());
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
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
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

    /**
     * 解析获取的Json数据,渲染到RecyclerView的每个Item中
     * @param jsonData
     */

    private void parseJsonObject(String jsonData) {
        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            String code = jsonObject.getString("code");
            String msg = jsonObject.getString("msg");

            if(code.equals("0")){
                String data = jsonObject.getString("data");
                JSONObject dataObject = new JSONObject(data);
                int total = dataObject.getInt("total");
                JSONArray labors = dataObject.getJSONArray("labors");
                items.clear();
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
                    items.add(labourItem);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 计算年份数组
    public ArrayList<String> calculateTerm(){
        int thisYear = DateUtils.getYear();  // 当前年份
        int thisMonth = DateUtils.getMonth();  // 当期月份
        ArrayList<String> term =  new ArrayList<>();
        int termLength;
        int startYear = 2020;  // 初始化值为2020，劳动学时从2020开始

        int currentYear = Integer.parseInt((String) SPUtils.get(mContext,"year", ""));  // 后端返回的学生当前年级
        String number = (String) SPUtils.get(mContext, "number", "");
        if(!number.equals("")){
            startYear = Integer.parseInt(number.substring(0,4));
        }
        int graduateYear = currentYear + 4;  // 毕业年份，学期时间数组截止年份 = 当前年份 < 毕业年份 ？当前年份
        if(thisYear < graduateYear){  // 未毕业，等于的话可能是毕业了
            if(thisMonth <= 1 || thisMonth >= 9){  // 第1学期
                termLength = (thisYear - startYear) * 2 - 1;
            } else {
                termLength = (thisYear - startYear) * 2;
            }
        }else {
            termLength = (graduateYear - startYear) * 2;
        }
        for(int i = 0; i < termLength; i++){
            int nextYear = startYear + 1;
            String termContent = (i % 2 == 0) ? startYear + "-" + nextYear + "-1"
                    : startYear + "-" + nextYear + "-2";
            term.add(termContent);
            if((i+1)%2 == 0) startYear++;
        }
        return term;
    }

    // 初始化弹出框的数据（包括学期，状态）
    private void initPopupData(View view){
        // 初始化时间
        ArrayList<String> termArr = calculateTerm();
        for (String term: termArr
             ) {
            Log.i("TAG", "initPopupData: " + term);
            Bean bean = new Bean();
            bean.setTermTime(term);
            timeData.add(bean);
        }

        // 默认显示弹出内容
        TextView time = view.findViewById(R.id.tv_term_show);
        int index = timeData.size()-1;
        String defaultTimeStr = timeData.get(index).getTermTime();
        time.setText(defaultTimeStr);

        // 初始化状态
        Bean end = new Bean();
        end.setStatus(-1);
        statusData.add(end);

        Bean start = new Bean();
        start.setStatus(0);
        statusData.add(start);

        Bean unStart = new Bean();
        unStart.setStatus(1);
        statusData.add(unStart);

        TextView status = view.findViewById(R.id.tv_status_show);
        int defaultStatusIndex = statusData.get(2).getStatus();
        String defaultStatus = statusArray[defaultStatusIndex + 1];
        status.setText(defaultStatus);

        // 设置初始化选择的Bean
        String[] timeArr = defaultTimeStr.split("-");
        String defaultTime = timeArr[0] + timeArr[1] + timeArr[2];
        chooseBean.setTermTime(defaultTime);
        chooseBean.setStatus(defaultStatusIndex);

    }

    /**
     * 初始化上拉刷新的控件
     * @param view
     */
    private void initSwipeRefreshLayout(View view) {
        refreshCurrent = view.findViewById(R.id.swipe_current_stu);
        refreshCurrent.setColorSchemeColors(getResources().getColor(R.color.right_bottom));
        refreshCurrent.setProgressBackgroundColorSchemeColor(getResources().getColor(R.color.white));
        // 初始化自动刷新
        refreshCurrent.setRefreshing(true);
        refreshCurrent.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            sendRequest(mContext, chooseBean);
            adapter.setItemLabourList(items);
            adapter.notifyDataSetChanged();
            refreshCurrent.setRefreshing(false);
        },800));
        if(refreshCurrent.isRefreshing()){
            countdown = new CountDownLatch(1);
            sendRequest(mContext, chooseBean);
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
                    refreshCurrent.setRefreshing(false);
                });
            }).start();
        }
    }

    /**
     * 初始化ToolBar
     * @param view
     */
    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.current_toolbar);
        toolbar.inflateMenu(R.menu.current_toolbar_menu);
        if (toolbar != null) {  //mActionBarToolbar就是android.support.v7.widget.Toolbar
            toolbar.setTitle("");  //设置为空，可以自己定义一个居中的控件，当做标题控件使用
        }

       //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.current_toolbar_menu, menu);
        initSearch(menu);
    }


    /**
     * 初始化搜索栏，未！！！！实现，有BUG
     * @param menu
     */
    private void initSearch(Menu menu){
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView)searchItem.getActionView();

        mSearchView.setSubmitButtonEnabled(true);

        mSearchView.setQueryHint("请输入搜索活动的关键字");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(mContext, newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

//        //添加点击查询的时候，有一个搜索框会显示
//        MenuItem item = menu.findItem(R.id.action_search);
//
//        //实例化一个查询视图
//        SearchView searchView = new SearchView(getContext());
//
//        //设置用户输入的文本
//        SpannableString spanText = new SpannableString("请输入搜索活动的关键字");
//
//        //设置成什么样子，起始下标，终止下标，标签
//        //第一个参数使用了前景色,
//        spanText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanText.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//
//        // 没有输入时的显示
//        searchView.setQueryHint("ddd");
//        item.setActionView(searchView);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
    }



    /**
     * 当前事件的Fragment中的学期选择下拉框
     * @param mView 控件
     * @param context 上下文
     */
    public void timeChoose(View mView, Context context){

        View popupView = getLayoutInflater().inflate(R.layout.pv_term_choose, null);

        ListView listView = popupView.findViewById(R.id.lv_term_choose);

        listView.setAdapter(new DropListViewAdapter(context, timeData, "termTime"));

        // 每个下拉框选项的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("TAG", "onItemClick: " + position);
                String timeShow = timeData.get(position).getTermTime();
                String[] tempTime = timeShow.split("-");
                String time = tempTime[0] + tempTime[1] + tempTime[2];
                chooseBean.setTermTime(time);

                // setText
                getActivity().runOnUiThread(() -> {
                    TextView tvTime = getActivity().findViewById(R.id.tv_term_show);
                    tvTime.setText(timeShow);
                });

                // 设置latch值为1，等待countDown方法执行
                countdown = new CountDownLatch(1);
                // 发送请求
                sendRequest(mContext, chooseBean);

                // 等待请求返回响应
                new Thread(() -> {
                    try {
                        countdown.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(() -> {
                        adapter.setItemLabourList(items);
                        adapter.notifyDataSetChanged();
                    });
                }).start();

                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOnDismissListener(() -> popupWindow.dismiss());
        popupWindow.showAsDropDown(mView,25, 0);
        popupWindow.setAnimationStyle(R.style.pop_menu_animation);
    }

     /**
     * 活动状态选择
     * @param mView 控件
     * @param context 上下文
     */
    public void statusChoose(View mView, Context context){
        View popupView = getLayoutInflater().inflate(R.layout.pv_status_choose, null);

        ListView listView = popupView.findViewById(R.id.lv_status_choose);
        listView.setAdapter(new DropListViewAdapter(context, statusData,"status"));


        //每个下拉框选项的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int status = statusData.get(position).getStatus();
                String statusStr = statusArray[status + 1];
                chooseBean.setStatus(status);

                // setText
                getActivity().runOnUiThread(() -> {
                    TextView tvStatus = getActivity().findViewById(R.id.tv_status_show);
                    tvStatus.setText(statusStr);
                });

                countdown = new CountDownLatch(1);
                // 发送请求
                sendRequest(mContext, chooseBean);
                new Thread(() -> {
                    try {
                        countdown.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(() -> {
                        adapter.setItemLabourList(items);
                        adapter.notifyDataSetChanged();
                    });
                }).start();

                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true); // 参数分别是 视图 长度 宽度 是否聚焦
        // 弹框消失监听器
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(mView,22,0);
        popupWindow.setAnimationStyle(R.style.pop_menu_animation);
    }

    // 重置活动列表
    private void resetList(Context mContext) {
        int checkedCount = 0;
        // 取消RadioButton的选中
        for(int i = 0; i < rgTypeSelect.getChildCount(); i++){
            RadioButton radioButton = (RadioButton) rgTypeSelect.getChildAt(i);
            if(radioButton.isChecked()){
                radioButton.setChecked(false);
                checkedCount++;
            }
        }
        Log.e("TAG", "resetList: "+ checkedCount);
        // 如果类型有选择，重置列表，展示全部类型活动
        if(checkedCount == 1){
            chooseBean.setType(0);
            // 设置latch值为1，等待countDown方法执行
            countdown = new CountDownLatch(1);

            sendRequest(mContext, chooseBean);

            // 等待请求返回响应
            new Thread(() -> {
                try {
                    countdown.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(() -> {
                    adapter.setItemLabourList(items);
                    adapter.notifyDataSetChanged();
                });
            }).start();
        }

    }

}