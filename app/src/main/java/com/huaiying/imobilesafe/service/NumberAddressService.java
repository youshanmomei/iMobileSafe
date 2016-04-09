package com.huaiying.imobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.huaiying.imobilesafe.db.AddressDao;
import com.huaiying.imobilesafe.ui.view.AddressToast;
import com.huaiying.imobilesafe.util.Logger;

public class NumberAddressService extends Service {

    private static final String TAG = "NumberAddressService";

    private AddressToast mCallInToast;
    private AddressToast mCallOutToast;

    //product manager definition:display only input
    private boolean showln = false;
    private TelephonyManager mTm;

    public NumberAddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mCallOutReveiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra(intent.EXTRA_PHONE_NUMBER);

            String address = AddressDao.findAddress(NumberAddressService.this, number);

            if (showln) {
                return;
            }
            mCallOutToast.show(address);
        }
    };

    private PhoneStateListener mCallInListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    mCallInToast.hide();
                    mCallOutToast.hide();

                    showln = false;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //display home
                    String address = AddressDao.findAddress(NumberAddressService.this, incomingNumber);

                    mCallInToast.show(address);
                    showln = true;
                    mCallInToast.hide();
                    break;
                case  TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(TAG, "归属地服务开启");

        mCallInToast = new AddressToast(this);
        mCallOutToast = new AddressToast(this);

        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTm.listen(mCallInListener, PhoneStateListener.LISTEN_CALL_STATE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mCallOutReveiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Logger.d(TAG, "归属地服务关闭");

        mTm.listen(mCallInListener, PhoneStateListener.LISTEN_NONE);
    }
}
