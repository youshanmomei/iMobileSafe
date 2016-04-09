package com.huaiying.imobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.service.GPSService;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

/**
 * Created by admin on 2016/4/9.
 */
public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "接收到的短信");
        Object[] objs = (Object[]) intent.getExtras().get("pdus");

        Logger.d(TAG, objs + "");

        boolean flag = SharePreferenceUtils.getBoolean(context, Constants.SJFD_PROTECTING);
        if (!flag) {
            return;
        }

        for (Object obj : objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

            String body = sms.getMessageBody();
            String sender = sms.getOriginatingAddress();

            Logger.d(TAG, "sender:" + sender);
            Logger.d(TAG, "body:" + body);

            if ("#*alarm*#".equals(body)) {
                Logger.d(TAG, "播放报警音乐");

                MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                player.setLooping(true);
                player.setVolume(1.0f, 1.0f);
                player.start();

                abortBroadcast();

            }else if ("#*location*#".equals(body)) {
                Logger.d(TAG, "gps追踪");

                Intent service = new Intent(context, GPSService.class);
                context.startService(service);

                abortBroadcast();
            }else if ("#*wpedata*#".equals(body)) {
                Logger.d(TAG, "远程擦除数据");

                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName who = new ComponentName(context, SafeAdimnReceiver.class);
                if (dpm.isAdminActive(who)) {
                    dpm.wipeData(0);
                }

                abortBroadcast();
            }else if ("#*lockscreen*#".equals(body)) {
                Logger.d(TAG, "锁屏");

                DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName who = new ComponentName(context, SafeAdimnReceiver.class);

                //if the device manage is activated
                if (dpm.isAdminActive(who)) {
                    dpm.resetPassword("123", 0);

                    dpm.lockNow();
                }

                abortBroadcast();
            }


        }
    }


}
