package com.huaiying.imobilesafe.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.AppInfo;
import com.huaiying.imobilesafe.db.AppLockDao;
import com.huaiying.imobilesafe.engine.AppInfoProvider;
import com.huaiying.imobilesafe.ui.view.SegmentControlView;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends AppCompatActivity {

    private SegmentControlView mSegementControlView;
    private TextView mTvTip;
    private ListView mLvUnlock;
    private ListView mLvlock;
    private LinearLayout mLoading;
    private List<AppInfo> mUnlockDatas;
    private List<AppInfo> mLockDatas;
    private boolean isAnimation;
    private AppLockDao mDao;
    private AppLockAdapter mUnlockAdapter;
    private AppLockAdapter mLockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        mDao = new AppLockDao(this);

        mSegementControlView = (SegmentControlView) findViewById(R.id.al_scv);
        mTvTip = (TextView) findViewById(R.id.al_tv_tip);
        mLvUnlock = (ListView) findViewById(R.id.al_lv_unlock);
        mLvlock = (ListView) findViewById(R.id.al_lv_lock);
        mLoading = (LinearLayout) findViewById(R.id.css_loading);

        mSegementControlView.setOnSelectedListener(new SegmentControlView.OnSelectedListener() {
            @Override
            public void onSaelected(boolean isLeftSelected) {
                if (isLeftSelected) {
                    mTvTip.setText("未加锁");
                    mLvUnlock.setVisibility(View.VISIBLE);
                    mLvlock.setVisibility(View.GONE);
                } else {
                    mTvTip.setText("已加锁");
                    mLvUnlock.setVisibility(View.GONE);
                    mLvlock.setVisibility(View.VISIBLE);
                }
            }
        });

        //loading list data
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
//                super.onPreExecute();
                mLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {
                //time-consuming operation
                List<AppInfo> allApps = AppInfoProvider.getAllApps(getApplicationContext());

                mUnlockDatas = new ArrayList<>();
                mLockDatas = new ArrayList<>();

                for (AppInfo info : allApps) {
                    if (mDao.findLock(info.packageName)) {
                        mLockDatas.add(info);
                    } else {
                        mUnlockDatas.add(info);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
//                super.onPostExecute(aVoid);
                mLoading.setVisibility(View.GONE);
                mTvTip.setText("未加锁（" + mUnlockDatas.size() + "）");

                mUnlockAdapter = new AppLockAdapter(false);
                mLvUnlock.setAdapter(mUnlockAdapter);

                mLockAdapter = new AppLockAdapter(true);
                mLvlock.setAdapter(mLockAdapter);
            }
        }.execute();


    }

    private class AppLockAdapter extends BaseAdapter{
        private boolean mLock;

        public AppLockAdapter(boolean lock) {
            this.mLock = lock;
        }

        @Override
        public int getCount() {
            if (mLock) {
                if (mLockDatas != null) {
                    mTvTip.setText("已加锁（" + mLockDatas.size() + "）");
                    return mLockDatas.size();
                } else {
                    mTvTip.setText("已加锁（0）");
                }
            } else {
                if (mUnlockDatas != null) {
                    mTvTip.setText("未加锁（" + mUnlockDatas.size() + "）");
                    return mUnlockDatas.size();
                } else {
                    mTvTip.setText("未加锁（0）");
                }
            }

            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mLock) {
                if (mLockDatas != null) {
                    return mLockDatas.get(position);
                }
            } else {
                if (mUnlockDatas != null) {
                    return mUnlockDatas.get(position);
                }
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_app_lock, null);

                holder = new ViewHolder();
                convertView.setTag(holder);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_al_iv_icon);
                holder.ivLock = (ImageView) convertView.findViewById(R.id.item_al_iv_lock);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_al_tv_name);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo info = null;
            if (mLock) {
                info = mLockDatas.get(position);
            } else {
                info = mUnlockDatas.get(position);
            }

            holder.ivIcon.setImageDrawable(info.icon);
            holder.tvName.setText(info.name);

            //set style
            if (mLock) {
                holder.ivLock.setImageResource(R.drawable.btn_unlock_selector);
            } else {
                holder.ivLock.setImageResource(R.drawable.btn_lock_selector);
            }

            final AppInfo app = info;
            final View view = convertView;
            holder.ivLock.setTag(app);
            holder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isAnimation) {
                        return;
                    }

                    if (mLock) {
                        TranslateAnimation ta = new TranslateAnimation(
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, -1,
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, 0
                        );
                        ta.setDuration(200);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isAnimation = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                AppInfo app = (AppInfo) ((ViewHolder) view.getTag()).ivLock.getTag();

                                if (mDao.delete(app.packageName)) {
                                    mLockDatas.remove(app);
                                    mUnlockDatas.add(app);

                                    mUnlockAdapter.notifyDataSetChanged();
                                    mLockAdapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(getApplicationContext(), "解锁失败", Toast.LENGTH_SHORT).show();
                                }
                                isAnimation = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        view.startAnimation(ta);
                    } else {
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, 1,
                                Animation.RELATIVE_TO_PARENT, 0,
                                Animation.RELATIVE_TO_PARENT, 0);
                        ta.setDuration(200);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isAnimation = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                AppInfo app = (AppInfo) ((ViewHolder) view.getTag()).ivLock.getTag();
                                if (mDao.add(app.packageName)) {
                                    mUnlockDatas.remove(app);
                                    mLockDatas.add(app);

                                    mUnlockAdapter.notifyDataSetChanged();
                                    mLockAdapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getApplicationContext(), "加锁失败", Toast.LENGTH_SHORT).show();
                                }
                                isAnimation = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(ta);
                    }
                }
            });
            
            return convertView;
        }

        private class ViewHolder{
            ImageView ivIcon;
            ImageView ivLock;
            TextView tvName;
        }
    }
}
