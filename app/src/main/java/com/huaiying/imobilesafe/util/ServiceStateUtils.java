package com.huaiying.imobilesafe.util;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

public class ServiceStateUtils {

	/**
	 * 判断服务是否运行
	 * 
	 * @param context
	 * @param clazz
	 * @return
	 */
	public static boolean isRunging(Context context,
			Class<? extends Service> clazz) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(Integer.MAX_VALUE);

		for (ActivityManager.RunningServiceInfo info : list) {
			ComponentName service = info.service;
			String className = service.getClassName();
			if (className.equals(clazz.getName())) {
				return true;
			}
		}

		return false;
	}
}
