package com.huaiying.imobilesafe.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.activity.SplahActivity;
import com.huaiying.imobilesafe.util.Logger;

import java.util.Timer;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by admin on 2016/3/8.
 */
public class ProtectedService extends Service {

    private static final String TAG = "ProtectedService";

    private static final int ID = 100;

    private Timer timer;

    private ScheduledFuture<?> furure;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "前台进程保护服务开启");

        //将服务做成前台进程
        Notification notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.tickerText = "小Q安全卫士保护您的安全";
        notification.contentView = new RemoteViews(getPackageName(), R.layout.notification_procted);

        Intent intent = new Intent(this, SplahActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        notification.contentIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        startForeground(ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "前台进程保护服务关闭");
    }
}
