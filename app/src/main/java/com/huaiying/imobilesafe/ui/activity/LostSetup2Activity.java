package com.huaiying.imobilesafe.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.base.BaseSetupActivity;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

public class LostSetup2Activity extends BaseSetupActivity {

    private RelativeLayout mRlBind;
    private ImageView mIvLock;
    private TelephonyManager mTm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_setup2);

        mRlBind = (RelativeLayout) findViewById(R.id.setup2_r1_bind);
        mIvLock = (ImageView) findViewById(R.id.setup2_iv_lock);

        //get system service to get telephonyManager
        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        //init UI
        String sim = SharePreferenceUtils.getString(LostSetup2Activity.this, Constants.SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {
            mIvLock.setImageResource(R.mipmap.unlock);
        } else {
            mIvLock.setImageResource(R.mipmap.lock);
        }

        mRlBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sim = SharePreferenceUtils.getString(LostSetup2Activity.this, Constants.SJFD_SIM);
                if (TextUtils.isEmpty(sim)) {
                    //if not bound, bind
                    //to store data, the current SIM card serial number

                    sim = mTm.getSimSerialNumber();
                    final String sim1 = sim;
                    SharePreferenceUtils.putString(LostSetup2Activity.this, Constants.SJFD_SIM, sim);

                    //UI
                    mIvLock.setImageResource(R.mipmap.lock);
                } else {
                    //if bound, unbind
                    //to store data, remove SIM card serial number
                    SharePreferenceUtils.putString(LostSetup2Activity.this, Constants.SJFD_SIM, null);

                    //UI
                    mIvLock.setImageResource(R.mipmap.unlock);

                }
            }
        });

    }

    @Override
    protected boolean performPre() {
        startActivity(new Intent(this, LostSetup1Activity.class));
        return false;
    }

    @Override
    protected boolean performNext() {
        //to detect whether the user is bound to the SIM card

        String sim = SharePreferenceUtils.getString(this, Constants.SJFD_SIM);
        if (TextUtils.isEmpty(sim)) {
            Toast.makeText(this, "如果要开启手机防盗，必须绑定手机sim", Toast.LENGTH_SHORT).show();
            //TODO in oorder to facilitate the development, first note off
//            return true;
        }

        startActivity(new Intent(this, LostSetup3Activity.class));

        return false;
    }
}
