package com.huaiying.imobilesafe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BlackEditActivity extends AppCompatActivity {

    public final static String ACTION_ADD = "add";
    public final static String ACTION_UPDATE = "update";
    public static final String EXTRA_NUMBEER = "number";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_POSITION = "position";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_edit);
    }
}
