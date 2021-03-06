package com.huaiying.imobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.widget.Toast;

import com.huaiying.imobilesafe.bean.ProcessInfo;
import com.huaiying.imobilesafe.engine.ProcessProvider;
import com.huaiying.imobilesafe.util.Logger;

import java.util.List;

/**
 * Created by admin on 2016/4/9.
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReveiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "执行任务");

        //clear memory
        int count = 0;
        long memory = 0;
        List<ProcessInfo> list = ProcessProvider.getAllRuningProcessed(context);
        for (ProcessInfo info : list) {
            if (!info.isForeground) {
                count++;
                memory += info.memory;
                ProcessProvider.killProcess(context, info.packageName);
            }
        }

        if (count > 0) {
            Toast.makeText(context, "清理了" + count + "进程，节约" + Formatter.formatFileSize(context, memory) + "内存", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "没有可清理的进程", Toast.LENGTH_SHORT).show();
        }

    }
}
