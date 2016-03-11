package com.huaiying.imobilesafe.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

public class LostFindActivity extends Activity {

    private TextView mTvNumber;
    private ImageView mIvProtecting;
    private RelativeLayout mRlProtecting;
    private RelativeLayout mRlSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_find);

        mTvNumber = (TextView) findViewById(R.id.lf_tv_number);
        String number = SharePreferenceUtils.getString(this, Constants.SJFD_NUMBER);
        mTvNumber.setText(number);

        mIvProtecting = (ImageView) findViewById(R.id.lf_iv_protecting);
        boolean protecting = SharePreferenceUtils.getBoolean(this, Constants.SJFD_PROTECTING);
        mIvProtecting.setImageResource(protecting ? R.mipmap.lock : R.mipmap.unlock);

        mRlProtecting = (RelativeLayout) findViewById(R.id.lf_rl_protecting);
        mRlProtecting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean protecting = SharePreferenceUtils.getBoolean(LostFindActivity.this, Constants.SJFD_PROTECTING);

                if (protecting) {
                    //如果是保护的，显示不保护
                    mIvProtecting.setImageResource(R.mipmap.unlock);
                    SharePreferenceUtils.putBoolean(LostFindActivity.this, Constants.SJFD_PROTECTING, false);
                } else {
                    mIvProtecting.setImageResource(R.mipmap.lock);
                    SharePreferenceUtils.putBoolean(LostFindActivity.this, Constants.SJFD_PROTECTING, true);
                }
            }
        });

        mRlSetup = (RelativeLayout) findViewById(R.id.lf_rl_setup);
        mRlSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LostFindActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
                finish();
            }
        });


    }
}
