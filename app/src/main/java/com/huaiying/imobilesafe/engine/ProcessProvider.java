package com.huaiying.imobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.ProcessInfo;
import com.huaiying.imobilesafe.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by admin on 2016/3/25.
 */
public class ProcessProvider {

    private static final String TAG = "ProcessProvider";

    /**
     * number of running processes
     * @param context
     * @return
     */
    public static int getRunningProgressCount(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            return runningAppProcesses.size();
        }

        return 0;
    }

    /**
     * get the total number of available process
     * @param context
     * @return
     */
    public static int getTotalProcessCount(Context context){
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packages = pm.getInstalledPackages(0);
        int count = 0;
        for (PackageInfo pack : packages) {
            //remove duplication
            HashSet<String> set = new HashSet<>();
            set.add(pack.applicationInfo.processName);

            //activity
            ActivityInfo[] activities = pack.activities;
            if (activities != null) {
                for (ActivityInfo activity : activities) {
                    String processName = activity.processName;
                    set.add(processName);
                }
            }

            //services
            ServiceInfo[] services = pack.services;
            if (services != null) {
                for (ServiceInfo service : services) {
                    String processName = service.processName;
                    set.add(processName);
                }
            }

            //receiver
            ActivityInfo[] receivers = pack.receivers;
            if (receivers != null) {
                for (ActivityInfo receiver : receivers) {
                    String processName = receiver.processName;
                    set.add(processName);
                }
            }

            //provider
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provi : providers) {
                    String processName = provi.processName;
                    set.add(processName);
                }
            }

            count += set.size();

        }

        return count;
    }

    /**
     * access to acailable memory infomation
     * @param context
     * @return
     */
    public static long getFreeMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);

        return outInfo.availMem;
    }

    /**
     * access to all memory
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Logger.d(TAG, "高版本总内存");
            return outInfo.totalMem;
        } else {
            Logger.d(TAG, "低版本总内存");
            return getLowTotalMemory();
        }
    }

    private static long getLowTotalMemory() {
        File file = new File("/proc/meminfo");
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            line = line.replace("MemTotal", "");
            line = line.replace("KB", "");
            line = line.trim();

            return Long.valueOf(line) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static List<ProcessInfo> getAllRuningProcessed(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        List<ProcessInfo> list = new ArrayList<ProcessInfo>();
        if (processes != null) {
            for (int i = 0; i < processes.size(); i++) {
                ActivityManager.RunningAppProcessInfo process = processes.get(i);

                String processName = process.processName;

                Drawable icon = null;
                String name = null;
                long memory = 0;
                boolean isSystem = false;

                try{
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(processName, 0);
                    icon = applicationInfo.loadIcon(pm);
                    name = applicationInfo.loadLabel(pm).toString();

                    int flags = applicationInfo.flags;

                    if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                        isSystem = true;
                    } else {
                        isSystem = false;
                    }
                }catch (Exception e){
//                    e.printStackTrace();
                    icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                    name = processName;
                    isSystem = true;

                }

                //memory info
                Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{process.pid})[0];
                memory = memoryInfo.getTotalPss() * 1024;

                ProcessInfo info = new ProcessInfo();
                info.icon = icon;
                info.name = name;
                info.memory = memory;
                info.isSystem = isSystem;
                info.packageName = processName;
                info.isForeground = process.importance == 100
                        || process.importance == 50
                        || process.importance == 130
                        || (info.isSystem && process.importance == 400);

                Logger.d(TAG, info.packageName + "==" + process.importance);
                list.add(info);
            }
        }

        return list;

    }

    public static void killProcess(Context context, String packageName){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        am.killBackgroundProcesses(packageName);
    }

}
