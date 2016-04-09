package com.huaiying.imobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.engine.ProcessProvider;
import com.huaiying.imobilesafe.receiver.ProcessWidgt;
import com.huaiying.imobilesafe.util.Logger;

public class UpdateWidgetService extends Service {

    private static final String TAG = "UpdateWidgetService";
    private AppWidgetManager mAwm;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "更新widget服务开启");

        mAwm = AppWidgetManager.getInstance(this);

        //start and update widget
        startUpdate();

    }


    private void startUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ComponentName provider = new ComponentName(UpdateWidgetService.this, ProcessWidgt.class);

                    //remote view
                    RemoteViews localRemoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);

                    //process count
                    localRemoteViews.setTextViewText(R.id.process_count, "正在运行的进程:" + ProcessProvider.getRunningProgressCount(UpdateWidgetService.this) + "个");

                    //memory
                    localRemoteViews.setTextViewText(R.id.process_memory, "可用内存:" + Formatter.formatFileSize(UpdateWidgetService.this, ProcessProvider.getFreeMemory(UpdateWidgetService.this)));

                    Intent intent = new Intent();
                    intent.setAction("xxxx.reveiver");

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(UpdateWidgetService.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    localRemoteViews.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                    mAwm.updateAppWidget(provider, localRemoteViews);

                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d(TAG, "更新widget服务关闭");
    }
}
