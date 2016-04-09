package com.huaiying.imobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.huaiying.imobilesafe.db.AppLockDao;
import com.huaiying.imobilesafe.ui.activity.LockScreenActivity;
import com.huaiying.imobilesafe.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService1 extends Service {

    private static final String TAG = "WatchDogService1";
    private ActivityManager mAm;
    private AppLockDao mDao;
    private List<String> mLockList;

    private List<String> mFreeList = new ArrayList<>();
    private boolean isRunning = false;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //stop time consuming operation
                isRunning = false;

                mFreeList.clear();
            }else if (action.equals("org.huaiying.free")) {
                String packageName = intent.getStringExtra(LockScreenActivity.EXTRA_PACKAGE_NAME);
            }else if (action.equals(Intent.ACTION_SCREEN_ON)) {
                startWatch();
            }

        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "开启电子狗1服务");

        //polling the current task stack
        mDao = new AppLockDao(this);
        mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mLockList = mDao.findAll();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.huaiying.free");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mReceiver, filter);

        ContentResolver cr = getContentResolver();
        cr.registerContentObserver(Uri.parse("content://com.huaiying.db.applock"), true, mObserver);

        startWatch();

    }

    private void startWatch() {
        if (isRunning) {
            return;
        }

        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Logger.d(TAG, "watch...");
                    //real time acquisition
                    List<ActivityManager.RunningTaskInfo> tasks = mAm.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo recentTask = tasks.get(0);

                    //the package name of the current application
                    String packageName = recentTask.topActivity.getPackageName();

                    if (mFreeList.contains(packageName)) {
                        continue;
                    }

                    if (mLockList.contains(packageName)) {
                        Intent intent = new Intent(WatchDogService1.this, LockScreenActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(LockScreenActivity.EXTRA_PACKAGE_NAME, packageName);
                        startActivity(intent);
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()){
        public void onChange(boolean selfChange) {
            mLockList = mDao.findAll();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d(TAG, "停止电子狗1服务");

        unregisterReceiver(mReceiver);
        getContentResolver().unregisterContentObserver(mObserver);
    }

    public WatchDogService1() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
