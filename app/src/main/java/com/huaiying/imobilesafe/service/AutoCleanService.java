package com.huaiying.imobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.huaiying.imobilesafe.bean.ProcessInfo;
import com.huaiying.imobilesafe.engine.ProcessProvider;
import com.huaiying.imobilesafe.util.Logger;

import java.util.List;

public class AutoCleanService extends Service {

    private static final String TAG = "AutoCleanService";

    public AutoCleanService() {
    }

    private BroadcastReceiver mReveicer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //lock screen
            //clean progress

            List<ProcessInfo> processes = ProcessProvider.getAllRuningProcessed(context);
            for (ProcessInfo info : processes) {
                ProcessProvider.killProcess(context, info.packageName);
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "开启锁屏清理的任务");

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReveicer, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d(TAG, "关闭锁屏清理");
        unregisterReceiver(mReveicer);
    }
}
