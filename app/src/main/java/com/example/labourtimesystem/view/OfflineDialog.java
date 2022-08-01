package com.example.labourtimesystem.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.labourtimesystem.R;


/**
 * @ProjectName : ForceOfflineActivity
 * @Author : Victor Scott
 * @Time : 2022/7/28 0:39
 * @Description : 自定义Dialog
 */
public class OfflineDialog extends Dialog {
    private Button confirm;
    private TextView title;
    private TextView message;
    private String titleStr;
    private String confirmStr;
    private String messageStr;
    private onConfirmOnclickListener confirmOnclickListener;


    public OfflineDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
    }


    public void setConfirmOnclickListener(String str, onConfirmOnclickListener onConfirmOnclickListener){
        if (str != null)
            confirmStr = str;
        this.confirmOnclickListener = onConfirmOnclickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_dialog);
        // 空白处不能取消动画
        setCanceledOnTouchOutside(false);

        // 初始化界面控件
        initView();

        // 初始化界面数据
        initData();

        // 初始化事件
        initEvent();
    }

    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confirmOnclickListener != null)
                    confirmOnclickListener.onConfirm();
            }
        });
    }

    private void initData() {
        if (titleStr != null) {
            title.setText(titleStr);
        }
        if (messageStr != null) {
            message.setText(messageStr);
        }
        if (confirmStr != null) {
            confirm.setText(confirmStr);
        }
    }

    private void initView() {
        title = findViewById(R.id.tv_title);
        message = findViewById(R.id.tv_tip);
        confirm = findViewById(R.id.btn_offline_confirm);
    }

    public interface onConfirmOnclickListener{
        void onConfirm();
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }
}
