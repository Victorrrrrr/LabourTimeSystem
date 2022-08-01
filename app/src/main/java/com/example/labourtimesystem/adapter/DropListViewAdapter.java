package com.example.labourtimesystem.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.labourtimesystem.bean.Bean;
import com.example.labourtimesystem.R;

import java.util.List;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/5/3 16:54
 * @Description : 描述
 */
public class DropListViewAdapter extends BaseAdapter {

    Context mContext;
    List<Bean> mData;
    LayoutInflater inflater;
    String mType;


    public DropListViewAdapter(Context context, List<Bean> data, String type){
        mContext = context;
        mData = data;
        inflater = LayoutInflater.from(mContext);
        mType = type;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // 每次加载一个item都会调用此方法
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_term_choose, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.tv_item_choose);
        // 根据不同的下拉框，设置不同的文本显示到页面
        if(mType.equals("termTime")) {
            textView.setText(mData.get(position).getTermTime());
        }else if(mType.equals("status")){
            String[] statusArray = new String[]{"已结束", "已开始",
                    "未开始"};
            textView.setText(statusArray[mData.get(position).getStatus() + 1]);
        }
        return convertView;
    }

}
