package com.example.labourtimesystem.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.labourtimesystem.HttpBinHelper;
import com.example.labourtimesystem.R;
import com.example.labourtimesystem.SettingActivity;

import com.example.labourtimesystem.adapter.DropListViewAdapter;
import com.example.labourtimesystem.adapter.ItemLabourRecyclerViewAdapter;
import com.example.labourtimesystem.bean.Bean;
import com.example.labourtimesystem.bean.LabourItem;
import com.example.labourtimesystem.bean.MyActivities;
import com.example.labourtimesystem.bean.MyActivity;
import com.example.labourtimesystem.databinding.FragmentStuMyBinding;
import com.example.labourtimesystem.decoration.SpaceItemDecoration;
import com.example.labourtimesystem.util.DateUtils;
import com.example.labourtimesystem.util.HttpUtils;
import com.example.labourtimesystem.util.SPUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class StuMyFragment extends Fragment{
    private FragmentStuMyBinding binding;
    private int chartPosition = -1;
    private Context mContext;
    private PopupWindow popupWindow;
    private List<Bean> timeData = new ArrayList<>();
    private static Bean chooseBean = new Bean();
    private CountDownLatch countdown;
    private ItemLabourRecyclerViewAdapter adapter;
    private RecyclerView rvJoined;
    private ArrayList<LabourItem> items = new ArrayList<>();
    private final String[] statusArray = new String[]{"已结束", "已开始",
            "未开始", "未开始", "未开始", "活动预告"};
    private final String[] typeArray = new String[]{"","日常性劳动",
            "创造性劳动", "服务性劳动", "其他劳动"};

    public StuMyFragment() {
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
        binding = FragmentStuMyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mContext = getContext();
        // 设置已参与的饼图
        setPieChartJoined();

        // 设置时间学时的饼图
        setPieChartTime();

        // 设置个人排名柱状图
        setHBarCharts();

        // 初始默认隐藏所有表格
        hideAllCharts(binding);

         // 设置表格按钮的点击事件
        setRadioCheck(binding);

        // 设置监听事件
        setRadioGroupCheckedListener((RadioGroup) binding.rgTypeSelect, binding);

        // 初始化学期数据
        initTermData(view);

        // 初始化RecyclerView列表
        initRecyclerViewList(binding);

        // 给学期选项框设置点击事件
        view.findViewById(R.id.rl_term_choose_my).setOnClickListener(v -> timeChoose(v, mContext));

        // 设置按钮跳转事件监听
        binding.ibMySetting.setOnClickListener(v -> {
            jumpToSetting();
        });

        // 显示用户名
        String username = (String) SPUtils.get(getContext(),"username","");
        binding.tvMyName.setText(username);

        // 显示学院
        String major = (String)SPUtils.get(getContext(),"major", "");
        binding.tvMyDepartment.setText(major);

        // 发送请求
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

        return view;
    }

    private void initRecyclerViewList(FragmentStuMyBinding binding) {
        RecyclerView recyclerView = binding.rvJoinedLabourActivity;
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelOffset(R.dimen.item_space)));
        adapter = new ItemLabourRecyclerViewAdapter(items, mContext);
        recyclerView.setAdapter(adapter);
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

    private void initTermData(View view){
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

        String[] timeArr = defaultTimeStr.split("-");
        String defaultTime = timeArr[0] + timeArr[1] + timeArr[2];
        chooseBean.setTermTime(defaultTime);

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
                Log.e("sss", "onItemClick: " + chooseBean.getTermTime());

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

        // 设置下拉框的样式
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // 设置下拉框的取消事件
        popupWindow.setOnDismissListener(() -> popupWindow.dismiss());
        // 设置下拉框的偏移
        popupWindow.showAsDropDown(mView,22, 0);
        // 设置下拉框的动画
        popupWindow.setAnimationStyle(R.style.pop_menu_animation);
    }


    // RadioGroup的选择事件
    private void setRadioCheck(FragmentStuMyBinding binding) {
        binding.rgTypeSelect.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.rb_joined:
                    isHiddenCharts(binding);
                    binding.pieChartJoined.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_time:
                    isHiddenCharts(binding);
                    binding.pieChartTime.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_rank:
                    isHiddenCharts(binding);
                    binding.llHorizontalBarChart.setVisibility(View.VISIBLE);
                    break;
                case R.id.rb_report:
                    isHiddenCharts(binding);
                    // 打印报告

                    break;
                default:
                    break;
            }
        });
    }

    private void sendRequest(Context mContext, Bean bean) {
        // 获取token
        String token = (String) SPUtils.get(mContext, "token", "");
        // 打包RequestBody
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jsonObject = new JSONObject();
        Log.i("sss", "sendRequest: "  + token + " " + bean.getTermTime());
        JSONObject data = new JSONObject();
        try {
            data.put("term", bean.getTermTime());
            jsonObject.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String body = String.valueOf(jsonObject);
        RequestBody requestBody =  RequestBody.create(mediaType, body);


        Retrofit retrofit = HttpUtils.initRetrofit(mContext);
        HttpBinHelper helper = retrofit.create(HttpBinHelper.class);
        Call<MyActivities> call = helper.getJoinActivity(token, requestBody);
        call.enqueue(new Callback<MyActivities>() {
            @Override
            public void onResponse(Call<MyActivities> call, Response<MyActivities> response) {
                if(response.body().getCode() == 0){
                    MyActivities myActivities = response.body();
                    Log.i("sss", myActivities != null ? myActivities.toString() : "null");
                    items.clear();
                    if( myActivities.getMyActivityList() != null ){
                        List<MyActivity> list =  myActivities.getMyActivityList();
                        Log.i("sss", "onResponse: " + list);
                        for(int i = 0; i < list.size(); i++) {
                            MyActivity myActivity = list.get(i);
                            int id = myActivity.getId();
                            String name = myActivity.getName();
                            String date = myActivity.getBeginTime().substring(0, 10);
                            String time = myActivity.getBeginTime().substring(11,19);
                            int capacity = myActivity.getCapacity();
                            int type = myActivity.getType();
                            String typeStr = typeArray[type];
                            String position = myActivity.getPosition();
                            String department = myActivity.getDepartment();
                            int status = myActivity.getStatus();
                            String statusStr = statusArray[status + 1];

                            LabourItem labourItem = new LabourItem(id, name, date, time, capacity,
                            typeStr, position, department, statusStr);
                            items.add(labourItem);
                        }
                    }

                }
                countdown.countDown();
            }

            @Override
            public void onFailure(Call<MyActivities> call, Throwable t) {
                t.printStackTrace();
                countdown.countDown();
            }
        });

    }



    // 初始化对话框
    private void initDialog(FragmentStuMyBinding binding) {
        Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.view_get_report_dialog ,null);

        mDialog.setContentView(dialogView);
        mDialog.setCancelable(false);
        mDialog.show();

        dialogView.findViewById(R.id.ib_dialog_report_cancel).setOnClickListener(v -> {
            mDialog.dismiss();
            ((RadioButton)binding.rbReport).setChecked(false);
        });

    }


    /**
     * 每个radioButton的点击监听事件
     * @param radioGroup
     * @param binding
     */
    private void setRadioGroupCheckedListener(final RadioGroup radioGroup, FragmentStuMyBinding binding){
        for(int i = 0; i < radioGroup.getChildCount()-1; i++){
            final RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            final int finalI = i;

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    radioGroup.clearCheck();
                    // 点击按钮为当前check的按钮，则取消check
                    if(chartPosition == finalI){
                        radioButton.setChecked(false);
                        chartPosition = -1;
                        hideAllCharts(binding);
                    }else {  // 否则正常选择
                        radioButton.setChecked(true);
                        chartPosition = finalI;
                    }
                }
            });
        }
        int rIndex = radioGroup.getChildCount() - 1;
        RadioButton rb = (RadioButton)radioGroup.getChildAt(rIndex);
        rb.setOnClickListener(v -> {
            initDialog(binding);
        });

    }


    private void isHiddenCharts(FragmentStuMyBinding binding) {
        if(binding.pieChartJoined.getVisibility() == View.VISIBLE) {
            binding.pieChartJoined.setVisibility(View.GONE);
        }
        if(binding.pieChartTime.getVisibility() == View.VISIBLE) {
            binding.pieChartTime.setVisibility(View.GONE);
        }
        if(binding.llHorizontalBarChart.getVisibility() == View.VISIBLE) {
            binding.llHorizontalBarChart.setVisibility(View.GONE);
        }
    }


    // 设置全部图表不可见
    private void hideAllCharts(FragmentStuMyBinding binding) {
        binding.pieChartJoined.setVisibility(View.GONE);
        binding.pieChartTime.setVisibility(View.GONE);
        binding.llHorizontalBarChart.setVisibility(View.GONE);
    }


    // 设置水平条状图，个人排名
    private void setHBarCharts() {
        List<BarEntry> list = new ArrayList<>();
        String[] values = new String[]{"next", "mine"};
        HorizontalBarChart barChart = binding.horizontalBarChart;
        float mine = 101;
        float next = 11;
        list.add(new BarEntry(0, next));
        list.add(new BarEntry(1, mine));


        BarDataSet barDataSet = new BarDataSet(list, "");
        barDataSet.setColor(Color.parseColor("#8991F3"));
        barDataSet.setFormLineWidth(0f);
        barDataSet.setFormSize(0f);
        barDataSet.setDrawValues(false);
        BarData barData= new BarData(barDataSet);
        barData.setValueTextSize(10f);
        barData.setValueTextColor(Color.WHITE);
        barData.setDrawValues(true);



        barChart.setData(barData);
        barChart.setPinchZoom(true);  //分别在x轴和y轴上进行缩放
        barChart.setDrawBarShadow(true);   //设置剩余统计图的阴影

        barChart.getDescription().setEnabled(false);  //隐藏右下角解释
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  // X轴的位置 默认为右边
        barChart.getAxisLeft().setEnabled(false);  //隐藏上侧Y轴   默认是上下两侧都有Y轴


        // initSetting
        XAxis mXAxis = barChart.getXAxis();
        //x轴设置显示位置在底部
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setGranularity(1f);
        mXAxis.setLabelCount(7);
        mXAxis.setTextSize(10f);

        //保证y轴从0开始 不然会上移一点
        AxisBase mAxisLeft = barChart.getAxisLeft();
        AxisBase mAxisRight = barChart.getAxisRight();
        mAxisLeft.setAxisMinimum(0f);
        mAxisRight.setAxisMinimum(0f);
        mAxisRight.enableGridDashedLine(10f,10f,10f);
        //mAxisLeft.setDrawAxisLine(false);
        mAxisRight.setDrawAxisLine(false);
        mAxisLeft.setDrawGridLines(false);
        mAxisRight.setDrawGridLines(false);
        //不显示左侧Y轴
        mAxisRight.setEnabled(false);


        // TODO:设置图表效果
        //不可以手动缩放
        barChart.setScaleXEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setScaleEnabled(false);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);

        barChart.setHighlightFullBarEnabled(false);
        //显示柱图底层阴影
        barChart.setDrawBarShadow(true);
        //最大显示值
        //setMaxVisibleValueCount(100);
        //限制长度在图表内部
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);

        barChart.setFitBars(true);
        //设置动画效果
        barChart.animateY(1000,Easing.Linear);
        barChart.animateX(1000,Easing.Linear);

        //不显示X轴 Y轴线条
        mXAxis.setDrawAxisLine(true);
        mXAxis.setDrawGridLines(false);
        mXAxis.setGridColor(Color.TRANSPARENT);

        Legend legend = barChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(0f);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(false);

        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int i = (int)value;
                String s = values[i];
                return s;
            }
        };
        mXAxis.setValueFormatter(xAxisFormatter);


    }

    // 设置饼图，参与实践
    private void setPieChartJoined() {
        List<PieEntry> entries = new ArrayList<>();
        int daily = 12;
        int create = 6;
        int service = 10;
        int other = 0;

        entries.add(new PieEntry(daily, "日常性劳动"));
        entries.add(new PieEntry(create, "创造性劳动"));
        entries.add(new PieEntry(service, "服务性劳动"));
        entries.add(new PieEntry(other, "其他劳动"));

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFC38A"));
        colors.add(Color.parseColor("#D3C2F9"));
        colors.add(Color.parseColor("#FFBBD0"));
        colors.add(Color.parseColor("#ADC8FD"));

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        // 设置数值样式
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);

        PieChart pieChart = binding.pieChartJoined;
        pieChart.setData(pieData);

        pieChart.setExtraOffsets(0f, 6f,0f,6f);

        // styling
        // 取消文字描述
        Description description = pieChart.getDescription();
        description.setEnabled(false);

        // 中间文字
        int time = daily + create + service + other;
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterText("实践次数\n" + time);
        pieChart.setCenterTextColor(Color.WHITE);

        // 中间的大饼颜色
        pieChart.setHoleColor(Color.parseColor("#00000000"));
        pieChart.setHoleRadius(70);

        // 半透明圆
        // pieChart.setTransparentCircleRadius(10);
        pieChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));

        // 设置可旋转
        pieChart.setRotationEnabled(true);

        // 设置Legend图例索引
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);  //设置注解的位置和形状
        legend.setTextSize(13f);
        legend.setTextColor(Color.WHITE);

        // 设置标签文字,这里取消设置
        pieChart.setEntryLabelTextSize(0f);

        // 设置动画
        pieChart.spin( 800,0,360f, Easing.EaseInOutQuad);

        pieChart.invalidate();   // refresh

        // 设置隐藏
        // pieChart.setVisibility(View.GONE);
    }

    // 设置饼图，计算学时
    private void setPieChartTime() {
        List<PieEntry> entries = new ArrayList<>();
        float daily = 2f;
        float create = 5f;
        float service = 2f;
        float other = -2f;
        float deduct = -5f;

        entries.add(new PieEntry(Math.abs(daily), "日常性劳动",String.valueOf(daily)));
        entries.add(new PieEntry(Math.abs(create), "创造性劳动",String.valueOf(create)));
        entries.add(new PieEntry(Math.abs(service), "服务性劳动",String.valueOf(service)));
        entries.add(new PieEntry(Math.abs(other), "其他劳动",String.valueOf(other)));
        entries.add(new PieEntry(Math.abs(deduct), "扣分劳动",String.valueOf(deduct)));

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFC38A"));
        colors.add(Color.parseColor("#D3C2F9"));
        colors.add(Color.parseColor("#FFBBD0"));
        colors.add(Color.parseColor("#ADC8FD"));
        colors.add(Color.parseColor("#BABCBB"));

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        // 设置数值样式
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);

        PieChart pieChart = binding.pieChartTime;
        pieChart.setData(pieData);

        pieChart.setExtraOffsets(0f, 6f,0f,6f);

        // styling
        // 取消文字描述
        Description description = pieChart.getDescription();
        description.setEnabled(false);

        // 中间文字
        float time = daily + create + service + other + deduct;
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterText("共计(h)\n" + time);
        pieChart.setCenterTextColor(Color.WHITE);

        // 中间的大饼颜色
        pieChart.setHoleColor(Color.parseColor("#00000000"));
        pieChart.setHoleRadius(70);

        // 半透明圆
        // pieChart.setTransparentCircleRadius(10);
        pieChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));

        // 设置可旋转
        pieChart.setRotationEnabled(true);

        // 设置Legend图例索引
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);  //设置注解的位置和形状
        legend.setTextSize(13f);
        legend.setTextColor(Color.WHITE);

        // 设置标签文字,这里取消设置
        pieChart.setEntryLabelTextSize(0f);

        // 设置动画
        pieChart.spin( 800,0,360f, Easing.EaseInOutQuad);

        pieChart.invalidate();   // refresh

        // 设置隐藏
        // pieChart.setVisibility(View.GONE);

    }


    private void jumpToSetting() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SettingActivity.class);
        startActivity(intent);
    }
}