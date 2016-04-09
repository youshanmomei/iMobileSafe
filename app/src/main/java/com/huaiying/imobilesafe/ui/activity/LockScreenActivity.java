package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.AppInfo;
import com.huaiying.imobilesafe.engine.AppInfoProvider;

public class LockScreenActivity extends AppCompatActivity {
    public static final String EXTRA_PACKAGE_NAME = "packageName";
    private EditText mEtPwd;
    private ImageView mIvIcon;
    private TextView mTvName;
    private String mPackageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        mEtPwd = (EditText) findViewById(R.id.ls_et_pwd);
        mIvIcon = (ImageView) findViewById(R.id.ls_iv_icon);
        mTvName = (TextView) findViewById(R.id.ls_tv_name);

        mPackageName = getIntent().getStringExtra(EXTRA_PACKAGE_NAME);
        AppInfo info = AppInfoProvider.getAppInfo(this, mPackageName);

        mIvIcon.setImageDrawable(info.icon);
        mTvName.setText(info.name);

    }

    public void clickOk(View view) {
        String pwd = mEtPwd.getText().toString().trim();

        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("123".equals(pwd)) {
            Intent intent = new Intent();
            intent.setAction("com.huaiying.free");
            intent.putExtra(EXTRA_PACKAGE_NAME, mPackageName);
            sendBroadcast(intent);

            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("andorid.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);

        finish();
    }
}
