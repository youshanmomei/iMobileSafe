package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.view.SettingItemView;

public class CommonToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_tool);

        SettingItemView mSivNumberAddress = (SettingItemView) findViewById(R.id.ct_siv_numberaddress);
        mSivNumberAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NumberAddressQueryActivity.class));
            }
        });
    }
}
