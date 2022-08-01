package com.example.labourtimesystem.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.labourtimesystem.MainActivity;
import com.example.labourtimesystem.view.OfflineDialog;

/**
 * @ProjectName : LabourTimeSystem
 * @Author : Victor Scott
 * @Time : 2022/7/28 13:46
 * @Description : 描述
 */
public class BaseActivity extends AppCompatActivity {

    private OfflineDialog offlineDialog;
    private ForceOfflineReceiver receiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.labourtimesystem.FORCE_OFFLINE");
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // 未实现
    public class ForceOfflineReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            offlineDialog = new OfflineDialog(context);
            offlineDialog.setTitle("强制下线");
            offlineDialog.setMessage("你的账号已在其他设备重复登录");
            offlineDialog.setConfirmOnclickListener("确定", new OfflineDialog.onConfirmOnclickListener() {
                @Override
                public void onConfirm() {
//                    Intent intent = new Intent(context , MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                }
            });
            offlineDialog.show();
        }
    }

}
