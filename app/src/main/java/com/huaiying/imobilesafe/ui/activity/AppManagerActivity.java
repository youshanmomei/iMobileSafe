package com.huaiying.imobilesafe.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.ui.view.ProgressDesView;

public class AppManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        //init view
        ProgressDesView mPdvRom = (ProgressDesView) findViewById(R.id.am_pdv_rom);

    }
}
