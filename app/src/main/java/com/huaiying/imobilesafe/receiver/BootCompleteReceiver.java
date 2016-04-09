package com.huaiying.imobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.huaiying.imobilesafe.service.ProtectedService;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

/**
 * Created by admin on 2016/4/9.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String TAG = "BottCompleteReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "接收到开机");

        boolean flag = SharePreferenceUtils.getBoolean(context, Constants.SJFD_PROTECTING);

        if (!flag) {
            return;
        }

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentSim = tm.getSimSerialNumber();
        String localSim = SharePreferenceUtils.getString(context, Constants.SJFD_SIM)+"xxx";

        if (!currentSim.equals(localSim)) {
            Logger.d(TAG, "手机可能被盗");

            //send message to safe number
            SmsManager sm = SmsManager.getDefault();
            String number = SharePreferenceUtils.getString(context, Constants.SJFD_NUMBER);
            sm.sendTextMessage(number, null, "shouji diu le ... XXXXXX", null, null);

        }

        //startServer
        context.startService(new Intent(context, ProtectedService.class));

    }
}
