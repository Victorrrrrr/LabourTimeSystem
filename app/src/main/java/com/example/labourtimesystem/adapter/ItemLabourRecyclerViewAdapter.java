package com.example.labourtimesystem.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.labourtimesystem.R;
import com.example.labourtimesystem.bean.LabourItem;

import java.util.ArrayList;

public class ItemLabourRecyclerViewAdapter extends RecyclerView.Adapter<ItemLabourRecyclerViewAdapter.ViewHolder> {

    private ArrayList<LabourItem> labourList;
    private final Context mContext;
    private OnItemClickListener onItemClickListener;

    public ItemLabourRecyclerViewAdapter(ArrayList<LabourItem> items, Context context) {
        this.labourList = items;
        this.mContext = context;
    }

    public void setItemLabourList(ArrayList<LabourItem> itemLabourList){
        this.labourList = itemLabourList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_labour_activity,parent,false);
        return new ViewHolder(view, onItemClickListener, labourList);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        LabourItem labourItem = labourList.get(position);

        holder.mItemTitle.setText(labourItem.getTitle());
        holder.mItemDate.setText(labourItem.getDate());
        holder.mItemTime.setText(labourItem.getTime());
        holder.mItemAccountNum.setText(String.valueOf(labourItem.getNumber()));
        holder.mItemType.setText(labourItem.getType());
        holder.mItemPlace.setText(labourItem.getPlace());
        holder.mItemDepartment.setText(labourItem.getDepartment());
        holder.mItemStatus.setText(labourItem.getStatus());

    }

    @Override
    public int getItemCount() {
        return labourList == null ? 0 : labourList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView mItemTitle;
        private final TextView mItemTime;
        private final TextView mItemDate;
        private final TextView mItemAccountNum;
        private final TextView mItemType;
        private final TextView mItemPlace;
        private final TextView mItemDepartment;
        private final TextView mItemStatus;
        private final ImageView mItemPic;
        private final LinearLayout mItem;
        OnItemClickListener onItemClickListener;
        private ArrayList<LabourItem> labourList;

        public ViewHolder(View itemView , OnItemClickListener onItemClickListener, ArrayList<LabourItem> items) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            this.labourList = items;
            mItemTitle = itemView.findViewById(R.id.item_title);
            mItemTime = itemView.findViewById(R.id.item_tv_labour_date);
            mItemDate = itemView.findViewById(R.id.item_tv_labour_time);
            mItemAccountNum = itemView.findViewById(R.id.item_tv_labour_account_num);
            mItemType = itemView.findViewById(R.id.item_tv_labour_type);
            mItemPlace = itemView.findViewById(R.id.item_tv_labour_place);
            mItemDepartment = itemView.findViewById(R.id.item_tv_labour_department);
            mItemPic = itemView.findViewById(R.id.item_iv_labour_pic);
            mItemStatus = itemView.findViewById(R.id.item_status);
            mItem = itemView.findViewById(R.id.item_labour_activity);
            mItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(v, getLayoutPosition(), labourList);
            }

        }
    }

    public interface OnItemClickListener {
        /**
         * 接口中的点击每一项的实现方法，参数自己定义
         * @param view 点击的item的视图
         * @param position 点击的item的id位置
         */
        void onItemClick(View view, int position, ArrayList<LabourItem> items);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }



}