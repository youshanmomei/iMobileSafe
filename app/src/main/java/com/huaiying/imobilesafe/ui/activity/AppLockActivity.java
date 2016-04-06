package com.huaiying.imobilesafe.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.db.AppLockDao;

public class AppLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        AppLockDao mDao = new AppLockDao(this);

    }
}
