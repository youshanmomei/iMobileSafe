package com.huaiying.imobilesafe.ui.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.receiver.SafeAdimnReceiver;
import com.huaiying.imobilesafe.ui.base.BaseSetupActivity;

public class LostSetup4Activity extends BaseSetupActivity {

    protected static final int REQUEST_CODE_ENABLE_ADMIN = 1010;
    private RelativeLayout mRlAdmin;
    private ImageView mIvAdmin;
    private DevicePolicyManager mDpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_setup4);

        mRlAdmin = (RelativeLayout) findViewById(R.id.setup4_rl_admin);
        mIvAdmin = (ImageView) findViewById(R.id.setup4_iv_admin);

        mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        final ComponentName who = new ComponentName(getApplicationContext(), SafeAdimnReceiver.class);

        //set initial state
        if (mDpm.isAdminActive(who)) {
            //active
            //change ui
            mIvAdmin.setImageResource(R.mipmap.admin_activated);
        } else {
            //change ui
            mIvAdmin.setImageResource(R.mipmap.admin_inactivated);
        }

        //set onclickListener
        mRlAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDpm.isAdminActive(who)) {
                    //if it is activated, cancel the activation
                    mDpm.removeActiveAdmin(who);

                    mDpm.resetPassword("", 0);

                    //change ui
                    mIvAdmin.setImageResource(R.mipmap.admin_inactivated);
                } else {
                    //need to active
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "小Q安全卫士");
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    //active success
                    //change ui
                    mIvAdmin.setImageResource(R.mipmap.admin_activated);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected boolean performPre() {
        startActivity(new Intent(this, LostSetup3Activity.class));
        return false;
    }

    @Override
    protected boolean performNext() {
        //check if the device manger is turned on
        ComponentName who = new ComponentName(getApplicationContext(), SafeAdimnReceiver.class);
        if (!mDpm.isAdminActive(who)) {
            Toast.makeText(getApplicationContext(), "要开启手机防盗必须甚至设备管理员", Toast.LENGTH_SHORT).show();
            return true;
        }

        //TODO LostSetup5
        startActivity(new Intent(this, MainActivity.class));
        return false;
    }
}
