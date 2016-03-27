package com.huaiying.imobilesafe.ui.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.huaiying.imobilesafe.R;

public class AntiVirusActivity extends AppCompatActivity {

    private PackageManager mPm;
    private TextView mTvPackageName;
    private ArcProgress mArcProgress;
    private ListView mListView;
    private RelativeLayout mRIProgressContainer;
    private LinearLayout mLIResultContainer;
    private LinearLayout mLIAnimatorContainer;
    private TextView mTvResult;
    private Button mBtnScan;
    private ImageView mIvLeft;
    private ImageView mIvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);

        mPm = getPackageManager();

        //init view
        mTvPackageName = (TextView) findViewById(R.id.aa_tv_packageName);
        mArcProgress = (ArcProgress) findViewById(R.id.aa_arc_progress);
        mListView = (ListView) findViewById(R.id.aa_listview);
        mRIProgressContainer = (RelativeLayout) findViewById(R.id.aa_progress_cotainer);
        mLIResultContainer = (LinearLayout) findViewById(R.id.aa_result_container);
        mLIAnimatorContainer = (LinearLayout) findViewById(R.id.aa_animator_container);
        mTvResult = (TextView) findViewById(R.id.aa_tv_result);
        mBtnScan = (Button) findViewById(R.id.aa_btn_scan);
//Todo        mBtnScan.setOnClickListener(this);

        mIvLeft = (ImageView) findViewById(R.id.aa_iv_left);
        mIvRight = (ImageView) findViewById(R.id.aa_iv_right);

//Todo        startScan();
    }
}
