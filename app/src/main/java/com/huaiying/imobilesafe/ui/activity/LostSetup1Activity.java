package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.base.BaseSetupActivity;

public class LostSetup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_setup1);
    }

    @Override
    protected boolean performPre() {
        return true;
    }

    @Override
    protected boolean performNext() {
        startActivity(new Intent(this, LostSetup2Activity.class));
        return false;
    }
}
