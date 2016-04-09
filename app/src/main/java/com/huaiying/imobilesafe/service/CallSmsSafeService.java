package com.huaiying.imobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.huaiying.imobilesafe.db.BlackDao;
import com.huaiying.imobilesafe.db.dao.BlackInfo;
import com.huaiying.imobilesafe.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSmsSafeService extends Service {
    private static final String TAG = "CallSmsSafeService";
    private TelephonyManager mTm;
    private BlackDao mDao;
    private TelephonyManager mTm1;

    public CallSmsSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PhoneStateListener mListenter = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
//            super.onCallStateChanged(state, incomingNumber);
            //state: the phone state

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    int type = mDao.findType(incomingNumber);
                    if (type == BlackInfo.TYPE_ALL || type == BlackInfo.TYPE_CALL) {
                        //need to intercept
                        //hang up
                        try {
                            Class<?> clazz = Class.forName("android.os.ServiceManage");
                            Method method = clazz.getDeclaredMethod("getService", String.class);
                            IBinder binder = (IBinder) method.invoke(null, Context.TELECOM_SERVICE);
                            ITelephony telephony = ITelephony.Stub.asInterface(binder);
                            telephony.endCall();


                            //delete call log
                            final ContentResolver cr = getContentResolver();
                            final Uri url = Uri.parse("content://call_log/calls");

                            cr.registerContentObserver(url, true, new ContentObserver(new Handler()) {
                                @Override
                                public void onChange(boolean selfChange) {
                                    String where = "number=?";
                                    String[] selectionArgs = new String[]{incomingNumber};
                                    cr.delete(url, where, selectionArgs);
                                }
                            });
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mSmsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            Object[] objs = (Object[]) extras.get("pdus");

            for (Object obj : objs) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) obj);
                String address = msg.getOriginatingAddress();

                //search
                int type = mDao.findType(address);
                if (type == BlackInfo.TYPE_SMS || type == BlackInfo.TYPE_ALL) {
                    abortBroadcast();
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "开启拦截服务");

        mTm1 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mDao = new BlackDao(this);

        //1.intercept phone
        mTm.listen(mListenter, PhoneStateListener.LISTEN_CALL_STATE);

        //2. intercept SMS
        //register SMS receiver
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSmsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d(TAG, "关闭拦截服务");

        //Log off monitor
        mTm.listen(mListenter, PhoneStateListener.LISTEN_NONE);

        //Log off
        unregisterReceiver(mSmsReceiver);
    }
}
