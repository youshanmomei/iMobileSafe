package com.huaiying.imobilesafe.ui.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.engine.SmsProvider;
import com.huaiying.imobilesafe.ui.view.SettingItemView;

public class CommonToolActivity extends AppCompatActivity {

    private SettingItemView mSivCommonNumber;
    private SettingItemView mSivSmsbackup;
    private SettingItemView mSivSmsrestore;

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

        mSivCommonNumber = (SettingItemView) findViewById(R.id.ct_siv_commonnum);
        mSivCommonNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CommonNumberActivity.class));
            }
        });

        mSivSmsbackup = (SettingItemView) findViewById(R.id.ct_siv_smsbackup);
        mSivSmsbackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsBackup();
            }
        });

        mSivSmsrestore = (SettingItemView) findViewById(R.id.ct_siv_smsrestorre);
        mSivSmsrestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsRestore();
            }
        });


    }

    private void smsBackup() {
        //pop up progress
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        SmsProvider.smsBackup(this, new SmsProvider.OnSmsListener() {
            @Override
            public void onMax(int max) {
                dialog.setMax(max);
            }

            @Override
            public void onProgress(int progress) {
                dialog.setProgress(progress);
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "备份成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onFailed() {
                Toast.makeText(getApplicationContext(), "备份失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

    }

    private void smsRestore(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        SmsProvider.smsRestore(this, new SmsProvider.OnSmsListener() {
            @Override
            public void onMax(int max) {
                dialog.setMax(max);
            }

            @Override
            public void onProgress(int progress) {
                dialog.setProgress(progress);
            }

            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "还原成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onFailed() {
                Toast.makeText(getApplicationContext(), "还原失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    public void clickAppLock(View view) {
        startActivity(new Intent(this, AppLockActivity.class));
    }
}
