package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.base.BaseSetupActivity;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

public class LostSetup5Activity extends BaseSetupActivity {

    private CheckBox mCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_setup5);
        mCb = (CheckBox) findViewById(R.id.setup5_cb);

        //init state
        boolean flag = SharePreferenceUtils.getBoolean(this, Constants.SJFD_PROTECTING);
        mCb.setChecked(flag);

        mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //mark whether open anti-theft function
                SharePreferenceUtils.putBoolean(LostSetup5Activity.this, Constants.SJFD_PASSWORD, isChecked);
            }
        });
    }

    @Override
    protected boolean performPre() {
        startActivity(new Intent(this, LostSetup4Activity.class));

        return false;
    }

    @Override
    protected boolean performNext() {
        //check whether the check button open
        if (!mCb.isChecked()) {
            Toast.makeText(this, "要开启防盗功能，必须勾选", Toast.LENGTH_SHORT).show();
            return true;
        }

        //mark has been set up
        SharePreferenceUtils.putBoolean(this, Constants.SJFD_SETUP, true);

        //to the page jump
        startActivity(new Intent(this, LostFindActivity.class));


        return false;
    }
}
