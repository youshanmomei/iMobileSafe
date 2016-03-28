package com.huaiying.imobilesafe.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.AntiVirusInfo;
import com.huaiying.imobilesafe.db.AntiVirusDao;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.MD5Utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AntiVirusActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AntiVirusActivity";
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

    private ScanTask mTask;
    private List<AntiVirusInfo> mDatas;
    private int mVirusTotal;

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
        mBtnScan.setOnClickListener(this);

        mIvLeft = (ImageView) findViewById(R.id.aa_iv_left);
        mIvRight = (ImageView) findViewById(R.id.aa_iv_right);

        startScan();
    }

    private void startScan() {
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnScan) {
            mBtnScan.setEnabled(false);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mIvLeft, "translationX", -mIvLeft.getWidth(), 0),
                    ObjectAnimator.ofFloat(mIvRight, "translationX", -mIvRight.getWidth(), 0),
                    ObjectAnimator.ofFloat(mIvLeft, "alpha", 0, 1),
                    ObjectAnimator.ofFloat(mIvRight, "alpha", 0, 1),
                    ObjectAnimator.ofFloat(mLIResultContainer, "alpha", 1, 0)
            );
            set.setDuration(2000);

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    startScan();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.start();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mTask != null) {
            mTask.stop();
            mTask = null;
        }
    }

    private class ScanTask extends AsyncTask<Void, AntiVirusInfo, Void> {
        private int progress;
        private int max;
        private boolean isFinish;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mRIProgressContainer.setVisibility(View.VISIBLE);
            mLIResultContainer.setVisibility(View.GONE);
            mLIResultContainer.setVisibility(View.GONE);
            mBtnScan.setEnabled(false);
        }

        public void stop() {
            isFinish = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            List<PackageInfo> list = mPm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

            //set maximum value
            max = list.size();

            mDatas = new ArrayList<AntiVirusInfo>();
            for (PackageInfo pack : list) {
                progress++;

                String sourceDir = pack.applicationInfo.sourceDir;

                FileInputStream in;
                try {
                    in = new FileInputStream(sourceDir);
                    String md5 = MD5Utils.encode(in);
                    String name = pack.applicationInfo.loadLabel(mPm).toString();
                    Drawable icon = pack.applicationInfo.loadIcon(mPm);
                    boolean isVirus = AntiVirusDao.isVirus(getApplicationContext(), md5);

                    Logger.d(TAG, "name:" + name);
                    Logger.d(TAG, "md5:" + md5);
                    Logger.d(TAG, "-----------");

                    AntiVirusInfo info = new AntiVirusInfo();
                    info.icon = icon;
                    info.name = name;
                    info.packageName = pack.packageName;
                    info.isVirus = isVirus;

                    //add to list
                    if (info.isVirus) {
                        mDatas.add(0, info);
                        mVirusTotal++;
                    } else {
                        mDatas.add(info);
                    }

                    //the progress of push
                    publishProgress(info);

                    if (isFinish) {
                        break;
                    }

                    Thread.sleep(100);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(AntiVirusInfo... values) {
            super.onProgressUpdate(values);
            //TODO ...
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
