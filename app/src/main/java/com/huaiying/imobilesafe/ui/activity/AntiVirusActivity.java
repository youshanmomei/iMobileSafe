package com.huaiying.imobilesafe.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private AntiVirusAdapter mAdapter;

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
//            super.onProgressUpdate(values);

            if (isFinish) {
                return;
            }

            AntiVirusInfo info = values[0];

            //change the package name and progress which scaned
            mTvPackageName.setText(info.packageName);
            int currentProgress = (int) (progress * 100f / max + 0.5f);
            mArcProgress.setProgress(currentProgress);

            if (progress == 1) {
                mAdapter = new AntiVirusAdapter();
                mListView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }

            mListView.smoothScrollToPosition(mAdapter.getCount());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            if (isFinish) {
                return;
            }

            //scroll to the top
            mListView.smoothScrollToPosition(0);

            if (mVirusTotal > 0) {
                mTvResult.setText("您的手机很不安全");
                mTvResult.setTextColor(Color.RED);
            } else {
                mTvResult.setText("您的手机很安全");
                mTvResult.setTextColor(Color.WHITE);
            }

            //in the end, do open animation
            //1.get the picture of the progress container
            mRIProgressContainer.setDrawingCacheEnabled(true);
            mRIProgressContainer.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = mRIProgressContainer.getDrawingCache();

            //set picture to the imageview
            mIvLeft.setImageBitmap(getLeftBitmap(bitmap));
            mIvRight.setImageBitmap(getRightBitmap(bitmap));

            //show the result, hidden the progress
            mRIProgressContainer.setVisibility(View.GONE);
            mLIResultContainer.setVisibility(View.VISIBLE);
            mLIAnimatorContainer.setVisibility(View.VISIBLE);
            mLIAnimatorContainer.bringToFront();

            showOpenAnimtor();

        }
    }

    private void showOpenAnimtor() {
        AnimatorSet set = new AnimatorSet();

        mLIAnimatorContainer.measure(0,0);
        set.playTogether(
                ObjectAnimator.ofFloat(mIvLeft, "translationX", 0, -mIvLeft.getMeasuredWidth()),
                ObjectAnimator.ofFloat(mIvRight, "translationX", 0, mIvRight.getMeasuredWidth()),
                ObjectAnimator.ofFloat(mIvLeft, "alpha", 1, 0),
                ObjectAnimator.ofFloat(mIvRight, "alpha", 1, 0),
                ObjectAnimator.ofFloat(mLIResultContainer, "alpha", 0, 1)
        );
        set.setDuration(3000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBtnScan.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        set.start();
    }

    private Bitmap getRightBitmap(Bitmap srcBitmap) {
        int width = (int) (srcBitmap.getWidth() / 2f + 0.5f);
        int height = srcBitmap.getHeight();
        Bitmap destBitmap = Bitmap.createBitmap(width, height, srcBitmap.getConfig());

        Canvas canvas = new Canvas(destBitmap);

        Paint paint = new Paint();

        Matrix matrix = new Matrix();
        matrix.setTranslate(-width, 0);

        canvas.drawBitmap(srcBitmap, matrix, paint);

        return destBitmap;

    }

    private Bitmap getLeftBitmap(Bitmap srcBitmap) {
        //1.prepare paper
        int width = (int) (srcBitmap.getWidth() / 2f + 0.5f);
        int height = srcBitmap.getHeight();
        Bitmap destBitmap = Bitmap.createBitmap(width, height, srcBitmap.getConfig());

        //2.prepare canvas, put paper on it
        Canvas canvas = new Canvas(destBitmap);

        //3.prepare paint
        Paint paint = new Paint();

        //4.prepare regular
        Matrix matrix = new Matrix();

        //5.paint
        canvas.drawBitmap(srcBitmap, matrix, paint);

        return destBitmap;

    }

    private class AntiVirusAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if (mDatas != null) {
                return mDatas.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_virus_info, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

                holder.ivClean = (ImageView) convertView.findViewById(R.id.item_virusinfo_iv_clean);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_virusinfo_iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_virusinfo_tv_name);
                holder.tvVirus = (TextView) convertView.findViewById(R.id.item_virusinfo_tv_virus);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //set data
            AntiVirusInfo info = mDatas.get(position);
            holder.ivIcon.setImageDrawable(info.icon);
            holder.tvName.setText(info.name);
            holder.tvVirus.setText(info.isVirus ? "病毒" : "安全");
            holder.tvVirus.setTextColor(info.isVirus ? Color.RED : Color.GREEN);
            holder.ivClean.setVisibility(info.isVirus?View.VISIBLE:View.GONE);


            return convertView;
        }

    }

    private class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvVirus;
        ImageView ivClean;
    }
}
