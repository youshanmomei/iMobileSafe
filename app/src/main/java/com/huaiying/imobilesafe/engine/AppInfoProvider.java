package com.huaiying.imobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.huaiying.imobilesafe.bean.AppInfo;
import com.huaiying.imobilesafe.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/3/22.
 */
public class AppInfoProvider {
    private static String TAG = "AppInfpProvider";

    public static List<AppInfo> getAllApps(Context context) {
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packages = pm.getInstalledPackages(0);
        List<AppInfo> list = new ArrayList<AppInfo>();

        for (PackageInfo pack : packages) {
            AppInfo info = new AppInfo();

            info.packageName = pack.packageName;

            ApplicationInfo applicationInfo = pack.applicationInfo;
            info.name = applicationInfo.loadLabel(pm).toString();
            info.icon = applicationInfo.loadIcon(pm);
            //applicationInfo.sourcceDir//->data/app/xxx.apk or system/app/xxx.apk
            //applicationInfo.dataDir//->data/data/packageName/

            String sourceDir = applicationInfo.sourceDir;
            info.size = new File(sourceDir).length();

            int flags = applicationInfo.flags;
            //whether is the system app FLAG_SYSTEM
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                info.isSystem = true;
            } else {
                info.isSystem = false;
            }


            //setup location
            //info.isInstallSD
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                info.isInstallSD = true;
            } else {
                info.isInstallSD = false;
            }

            Logger.d(TAG, "" + info.packageName);
            Logger.d(TAG, "" + info.name);
            Logger.d(TAG, "" + info.size);
            Logger.d(TAG, "系统：" + info.isSystem);
            Logger.d(TAG, "-----------------------");


            list.add(info);
        }

        return list;
    }

    public static AppInfo getAppInfo(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);

            AppInfo info = new AppInfo();
            info.packageName = packageName;
            info.name = applicationInfo.loadLabel(pm).toString();
            info.icon = applicationInfo.loadIcon(pm);

            int flags = applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                info.isSystem = true;
            } else {
                info.isSystem = false;
            }

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                info.isInstallSD = true;
            } else {
                info.isInstallSD = false;
            }

            return info;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
