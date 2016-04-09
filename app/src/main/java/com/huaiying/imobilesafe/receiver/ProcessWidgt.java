package com.huaiying.imobilesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.huaiying.imobilesafe.service.UpdateWidgetService;
import com.huaiying.imobilesafe.util.Logger;

/**
 * Created by admin on 2016/4/9.
 */
public class ProcessWidgt extends AppWidgetProvider {
    private static final String TAG = "ProcessWidge";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Logger.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Logger.d(TAG,"onDisable");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Logger.d(TAG, "onUpdate");

        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }
}
