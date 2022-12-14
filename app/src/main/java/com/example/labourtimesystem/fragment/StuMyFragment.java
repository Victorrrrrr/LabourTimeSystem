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
    private final String[] statusArray = new String[]{"?????????", "?????????",
            "?????????", "?????????", "?????????", "????????????"};
    private final String[] typeArray = new String[]{"","???????????????",
            "???????????????", "???????????????", "????????????"};

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
        // ????????????????????????
        setPieChartJoined();

        // ???????????????????????????
        setPieChartTime();

        // ???????????????????????????
        setHBarCharts();

        // ??????????????????????????????
        hideAllCharts(binding);

         // ?????????????????????????????????
        setRadioCheck(binding);

        // ??????????????????
        setRadioGroupCheckedListener((RadioGroup) binding.rgTypeSelect, binding);

        // ?????????????????????
        initTermData(view);

        // ?????????RecyclerView??????
        initRecyclerViewList(binding);

        // ????????????????????????????????????
        view.findViewById(R.id.rl_term_choose_my).setOnClickListener(v -> timeChoose(v, mContext));

        // ??????????????????????????????
        binding.ibMySetting.setOnClickListener(v -> {
            jumpToSetting();
        });

        // ???????????????
        String username = (String) SPUtils.get(getContext(),"username","");
        binding.tvMyName.setText(username);

        // ????????????
        String major = (String)SPUtils.get(getContext(),"major", "");
        binding.tvMyDepartment.setText(major);

        // ????????????
        countdown = new CountDownLatch(1);
        sendRequest(mContext, chooseBean);

        // ????????????????????????
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

    // ??????????????????
    public ArrayList<String> calculateTerm(){
        int thisYear = DateUtils.getYear();  // ????????????
        int thisMonth = DateUtils.getMonth();  // ????????????
        ArrayList<String> term =  new ArrayList<>();
        int termLength;
        int startYear = 2020;  // ???????????????2020??????????????????2020??????

        int currentYear = Integer.parseInt((String) SPUtils.get(mContext,"year", ""));  // ?????????????????????????????????
        String number = (String) SPUtils.get(mContext, "number", "");
        if(!number.equals("")){
            startYear = Integer.parseInt(number.substring(0,4));
        }
        int graduateYear = currentYear + 4;  // ????????????????????????????????????????????? = ???????????? < ???????????? ???????????????
        if(thisYear < graduateYear){  // ??????????????????????????????????????????
            if(thisMonth <= 1 || thisMonth >= 9){  // ???1??????
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
        // ???????????????
        ArrayList<String> termArr = calculateTerm();
        for (String term: termArr
        ) {
            Log.i("TAG", "initPopupData: " + term);
            Bean bean = new Bean();
            bean.setTermTime(term);
            timeData.add(bean);
        }

        // ????????????????????????
        TextView time = view.findViewById(R.id.tv_term_show);
        int index = timeData.size()-1;
        String defaultTimeStr = timeData.get(index).getTermTime();
        time.setText(defaultTimeStr);

        String[] timeArr = defaultTimeStr.split("-");
        String defaultTime = timeArr[0] + timeArr[1] + timeArr[2];
        chooseBean.setTermTime(defaultTime);

    }


    /**
     * ???????????????Fragment???????????????????????????
     * @param mView ??????
     * @param context ?????????
     */
    public void timeChoose(View mView, Context context){

        View popupView = getLayoutInflater().inflate(R.layout.pv_term_choose, null);

        ListView listView = popupView.findViewById(R.id.lv_term_choose);

        listView.setAdapter(new DropListViewAdapter(context, timeData, "termTime"));

        // ????????????????????????????????????
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

                // ??????latch??????1?????????countDown????????????
                countdown = new CountDownLatch(1);
                // ????????????
                sendRequest(mContext, chooseBean);

                // ????????????????????????
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

        // ????????????????????????
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // ??????????????????????????????
        popupWindow.setOnDismissListener(() -> popupWindow.dismiss());
        // ????????????????????????
        popupWindow.showAsDropDown(mView,22, 0);
        // ????????????????????????
        popupWindow.setAnimationStyle(R.style.pop_menu_animation);
    }


    // RadioGroup???????????????
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
                    // ????????????

                    break;
                default:
                    break;
            }
        });
    }

    private void sendRequest(Context mContext, Bean bean) {
        // ??????token
        String token = (String) SPUtils.get(mContext, "token", "");
        // ??????RequestBody
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



    // ??????????????????
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
     * ??????radioButton?????????????????????
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
                    // ?????????????????????check?????????????????????check
                    if(chartPosition == finalI){
                        radioButton.setChecked(false);
                        chartPosition = -1;
                        hideAllCharts(binding);
                    }else {  // ??????????????????
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


    // ???????????????????????????
    private void hideAllCharts(FragmentStuMyBinding binding) {
        binding.pieChartJoined.setVisibility(View.GONE);
        binding.pieChartTime.setVisibility(View.GONE);
        binding.llHorizontalBarChart.setVisibility(View.GONE);
    }


    // ????????????????????????????????????
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
        barChart.setPinchZoom(true);  //?????????x??????y??????????????????
        barChart.setDrawBarShadow(true);   //??????????????????????????????

        barChart.getDescription().setEnabled(false);  //?????????????????????
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);  // X???????????? ???????????????
        barChart.getAxisLeft().setEnabled(false);  //????????????Y???   ???????????????????????????Y???


        // initSetting
        XAxis mXAxis = barChart.getXAxis();
        //x??????????????????????????????
        mXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxis.setGranularity(1f);
        mXAxis.setLabelCount(7);
        mXAxis.setTextSize(10f);

        //??????y??????0?????? ?????????????????????
        AxisBase mAxisLeft = barChart.getAxisLeft();
        AxisBase mAxisRight = barChart.getAxisRight();
        mAxisLeft.setAxisMinimum(0f);
        mAxisRight.setAxisMinimum(0f);
        mAxisRight.enableGridDashedLine(10f,10f,10f);
        //mAxisLeft.setDrawAxisLine(false);
        mAxisRight.setDrawAxisLine(false);
        mAxisLeft.setDrawGridLines(false);
        mAxisRight.setDrawGridLines(false);
        //???????????????Y???
        mAxisRight.setEnabled(false);


        // TODO:??????????????????
        //?????????????????????
        barChart.setScaleXEnabled(false);
        barChart.setScaleYEnabled(false);
        barChart.setScaleEnabled(false);

        barChart.setDrawGridBackground(false);
        barChart.setDrawBorders(false);

        barChart.setHighlightFullBarEnabled(false);
        //????????????????????????
        barChart.setDrawBarShadow(true);
        //???????????????
        //setMaxVisibleValueCount(100);
        //???????????????????????????
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);

        barChart.setFitBars(true);
        //??????????????????
        barChart.animateY(1000,Easing.Linear);
        barChart.animateX(1000,Easing.Linear);

        //?????????X??? Y?????????
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

    // ???????????????????????????
    private void setPieChartJoined() {
        List<PieEntry> entries = new ArrayList<>();
        int daily = 12;
        int create = 6;
        int service = 10;
        int other = 0;

        entries.add(new PieEntry(daily, "???????????????"));
        entries.add(new PieEntry(create, "???????????????"));
        entries.add(new PieEntry(service, "???????????????"));
        entries.add(new PieEntry(other, "????????????"));

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFC38A"));
        colors.add(Color.parseColor("#D3C2F9"));
        colors.add(Color.parseColor("#FFBBD0"));
        colors.add(Color.parseColor("#ADC8FD"));

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        // ??????????????????
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);

        PieChart pieChart = binding.pieChartJoined;
        pieChart.setData(pieData);

        pieChart.setExtraOffsets(0f, 6f,0f,6f);

        // styling
        // ??????????????????
        Description description = pieChart.getDescription();
        description.setEnabled(false);

        // ????????????
        int time = daily + create + service + other;
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterText("????????????\n" + time);
        pieChart.setCenterTextColor(Color.WHITE);

        // ?????????????????????
        pieChart.setHoleColor(Color.parseColor("#00000000"));
        pieChart.setHoleRadius(70);

        // ????????????
        // pieChart.setTransparentCircleRadius(10);
        pieChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));

        // ???????????????
        pieChart.setRotationEnabled(true);

        // ??????Legend????????????
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);  //??????????????????????????????
        legend.setTextSize(13f);
        legend.setTextColor(Color.WHITE);

        // ??????????????????,??????????????????
        pieChart.setEntryLabelTextSize(0f);

        // ????????????
        pieChart.spin( 800,0,360f, Easing.EaseInOutQuad);

        pieChart.invalidate();   // refresh

        // ????????????
        // pieChart.setVisibility(View.GONE);
    }

    // ???????????????????????????
    private void setPieChartTime() {
        List<PieEntry> entries = new ArrayList<>();
        float daily = 2f;
        float create = 5f;
        float service = 2f;
        float other = -2f;
        float deduct = -5f;

        entries.add(new PieEntry(Math.abs(daily), "???????????????",String.valueOf(daily)));
        entries.add(new PieEntry(Math.abs(create), "???????????????",String.valueOf(create)));
        entries.add(new PieEntry(Math.abs(service), "???????????????",String.valueOf(service)));
        entries.add(new PieEntry(Math.abs(other), "????????????",String.valueOf(other)));
        entries.add(new PieEntry(Math.abs(deduct), "????????????",String.valueOf(deduct)));

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#FFC38A"));
        colors.add(Color.parseColor("#D3C2F9"));
        colors.add(Color.parseColor("#FFBBD0"));
        colors.add(Color.parseColor("#ADC8FD"));
        colors.add(Color.parseColor("#BABCBB"));

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        // ??????????????????
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);

        PieChart pieChart = binding.pieChartTime;
        pieChart.setData(pieData);

        pieChart.setExtraOffsets(0f, 6f,0f,6f);

        // styling
        // ??????????????????
        Description description = pieChart.getDescription();
        description.setEnabled(false);

        // ????????????
        float time = daily + create + service + other + deduct;
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterText("??????(h)\n" + time);
        pieChart.setCenterTextColor(Color.WHITE);

        // ?????????????????????
        pieChart.setHoleColor(Color.parseColor("#00000000"));
        pieChart.setHoleRadius(70);

        // ????????????
        // pieChart.setTransparentCircleRadius(10);
        pieChart.setTransparentCircleColor(Color.parseColor("#FFFFFF"));

        // ???????????????
        pieChart.setRotationEnabled(true);

        // ??????Legend????????????
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setForm(Legend.LegendForm.SQUARE);  //??????????????????????????????
        legend.setTextSize(13f);
        legend.setTextColor(Color.WHITE);

        // ??????????????????,??????????????????
        pieChart.setEntryLabelTextSize(0f);

        // ????????????
        pieChart.spin( 800,0,360f, Easing.EaseInOutQuad);

        pieChart.invalidate();   // refresh

        // ????????????
        // pieChart.setVisibility(View.GONE);

    }


    private void jumpToSetting() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SettingActivity.class);
        startActivity(intent);
    }
}