package com.huaiying.imobilesafe.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.ProcessInfo;
import com.huaiying.imobilesafe.engine.ProcessProvider;
import com.huaiying.imobilesafe.service.AutoCleanService;
import com.huaiying.imobilesafe.ui.view.ProgressDesView;
import com.huaiying.imobilesafe.ui.view.SettingItemView;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.ServiceStateUtils;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProcessManagerActivity extends Activity {

    private final String TAG = "ProcessManagerActivity";

    private List<ProcessInfo> mDatas;

    private ProcessAdapter mAdapter;
    private ProgressDesView mPdvProgress;
    private ProgressDesView mPdvMemory;
    private StickyListHeadersListView mListView;
    private LinearLayout mLoading;
    private ImageView mlvArrow1;
    private ImageView mlvArrow2;
    private SlidingDrawer mDrawer;
    private SettingItemView mSivShowSystem;
    private SettingItemView mSivAutoClean;
    private boolean showSystem;
    private int mRunningProcessCount;
    private int mTotalProcessCount;
    private long mFreeMemory;
    private long mTotalMemory;
    private List<ProcessInfo> mSystemDatas;
    private List<ProcessInfo> mUserDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        //init view
        mPdvProgress = (ProgressDesView) findViewById(R.id.pm_pdv_progress);
        mPdvMemory = (ProgressDesView) findViewById(R.id.pm_pdv_memory);
        mListView = (StickyListHeadersListView) findViewById(R.id.pm_listview);
        mLoading = (LinearLayout) findViewById(R.id.css_loading);
        mlvArrow1 = (ImageView) findViewById(R.id.pm_drawer_arrow1);
        mlvArrow2 = (ImageView) findViewById(R.id.pm_drawer_arrow2);
        mDrawer = (SlidingDrawer) findViewById(R.id.pm_drawer);
        mSivShowSystem = (SettingItemView) findViewById(R.id.pm_siv_showsystem);
        mSivAutoClean = (SettingItemView) findViewById(R.id.pm_siv_autoclean);

        //set the listview item event
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProcessInfo info = mDatas.get(position);
                if (info.packageName.equals(getPackageName())) return;

                //if checked, cancel
                info.checked = !info.checked;

                //updata UI
                mAdapter.notifyDataSetChanged();
            }
        });

        //let the arrow do animation
        showUPAnimation();

        //set drawer listener
        mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                //the callback of the drawer open
                showDownArrow();
            }
        });

        mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                showUPAnimation();
            }
        });

        //set system progress open and close state
        showSystem = SharePreferenceUtils.getBoolean(getApplicationContext(), Constants.SHOW_SYSTEM_PROCESS, true);
        mSivShowSystem.setToggleOn(showSystem);

        //set show system progress event
        mSivShowSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = SharePreferenceUtils.getBoolean(getApplicationContext(), Constants.SHOW_SYSTEM_PROCESS, true);

                //update UI
                mSivShowSystem.setToggleOn(!flag);

                showSystem = !flag;
                mAdapter.notifyDataSetChanged();

                //update data
                SharePreferenceUtils.putBoolean(getApplicationContext(), Constants.SHOW_SYSTEM_PROCESS, !flag);
            }
        });

        //set the lock screen to automatically clean the click event
        mSivAutoClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean running = ServiceStateUtils.isRunging(getApplicationContext(), AutoCleanService.class);
                if (running) {
                    stopService(new Intent(getApplicationContext(), AutoCleanService.class));
                } else {
                    startService(new Intent(getApplicationContext(), AutoCleanService.class));
                }

                //ui
                mSivAutoClean.setToggleOn(!running);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //initialize service state
        boolean runging = ServiceStateUtils.isRunging(getApplicationContext(), AutoCleanService.class);
        mSivAutoClean.setToggleOn(runging);

        //progress
        //number of running processed, total number progress
        mRunningProcessCount = ProcessProvider.getRunningProgressCount(this);
        mTotalProcessCount = ProcessProvider.getTotalProcessCount(this);
        initProcessUI();

        //memory
        mFreeMemory = ProcessProvider.getFreeMemory(this);
        mTotalMemory = ProcessProvider.getTotalMemory(this);
        intiMemoryUI();

        startQuery();
    }

    private void startQuery() {
        mLoading.setVisibility(View.VISIBLE);

        //open thread loading
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {Thread.sleep(1200);} catch (InterruptedException e) {e.printStackTrace(); }

                //load data
                mDatas = ProcessProvider.getAllRuningProcessed(getApplicationContext());

                mSystemDatas = new ArrayList<ProcessInfo>();
                mUserDatas = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : mDatas) {
                    if (info.isSystem) {
                        mSystemDatas.add(info);
                    } else {
                        mUserDatas.add(info);
                    }
                }

                mDatas.clear();
                mDatas.addAll(mUserDatas);
                mDatas.addAll(mSystemDatas);

                //set adapter in main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLoading.setVisibility(View.GONE);

                        //setData to listView
                        mAdapter = new ProcessAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });


            }
        }).start();
    }

    private void intiMemoryUI() {
        long usedMemory = mTotalMemory - mFreeMemory;

        mPdvMemory.setDesTitle("内存：");
        mPdvMemory.setDesLeft("内存占用：" + Formatter.formatFileSize(this, usedMemory));
        mPdvMemory.setDesRight("可用内存：" + Formatter.formatFileSize(this, mFreeMemory));
        mPdvMemory.setDesProgress((int) (usedMemory * 100f / mTotalMemory + 0.5f));
    }


    private void initProcessUI() {
        mPdvProgress.setDesTitle("进程数");
        mPdvProgress.setDesLeft("正在运行" + mRunningProcessCount + "个");
        mPdvProgress.setDesRight("可用进程" + mTotalProcessCount + "个");
        mPdvProgress.setDesProgress((int)(mRunningProcessCount * 100f/mTotalProcessCount+0.5f));
    }

    private void showDownArrow() {
        mlvArrow1.clearAnimation();
        mlvArrow2.clearAnimation();

        mlvArrow1.setImageResource(R.mipmap.drawer_arrow_down);
        mlvArrow2.setImageResource(R.mipmap.drawer_arrow_down);
    }

    private void showUPAnimation() {
        mlvArrow1.setImageResource(R.mipmap.drawer_arrow_up);
        mlvArrow2.setImageResource(R.mipmap.drawer_arrow_up);

        AlphaAnimation alpha1 = new AlphaAnimation(0.2f, 1f);
        alpha1.setDuration(600);
        alpha1.setRepeatCount(AlphaAnimation.INFINITE);
        alpha1.setRepeatMode(AlphaAnimation.REVERSE);
        mlvArrow1.startAnimation(alpha1);

        AlphaAnimation alpha2 = new AlphaAnimation(1f, 0.2f);
        alpha2.setDuration(600);
        alpha2.setRepeatCount(AlphaAnimation.INFINITE);
        alpha2.setRepeatMode(AlphaAnimation.REVERSE);
        mlvArrow2.startAnimation(alpha2);
    }

    public void clickAll(View view) {
        if (mDatas == null) {
            return;
        }

        if (showSystem) {
            for (ProcessInfo info : mDatas) {
                if (info.packageName.equals(getPackageName())) {
                    continue;
                }
                info.checked = true;
            }
        } else {
            for (ProcessInfo info : mUserDatas) {
                if (info.packageName.equals(getPackageName())) {
                    continue;
                }
                info.checked = true;
            }
        }

        mAdapter.notifyDataSetChanged();

    }

    public void clickReverse(View view) {
        if (mDatas == null) {
            return;
        }

        if (showSystem) {
            for (ProcessInfo info : mDatas) {
                if (info.packageName.equals(getPackageName())) {
                    continue;
                }
                info.checked = !info.checked;
            }
        } else {
            for (ProcessInfo info : mUserDatas) {
                if (info.packageName.equals(getPackageName())) {
                    continue;
                }
                info.checked = !info.checked;
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    public void clickClean(View view) {
        if (mDatas == null) {
            return;
        }

        int processCount = 0;
        int freeMemory = 0;

        ListIterator<ProcessInfo> iterator = null;
        if (showSystem) {
            iterator = mDatas.listIterator();
        } else {
            iterator = mUserDatas.listIterator();
        }

        while (iterator.hasNext()) {
            ProcessInfo info = iterator.next();
            if (info.checked) {
                //kill
                ProcessProvider.killProcess(this, info.packageName);
                iterator.remove();

                if (showSystem) {
                    mUserDatas.remove(info);
                } else {
                    mDatas.remove(info);
                }

                processCount++;
                freeMemory += info.memory;

            }

        }

        if (processCount != 0) {
            //clean number of process, release memory
            Toast.makeText(this, "结束了" + processCount + "进程，释放了" + Formatter.formatShortFileSize(this, freeMemory) + "内存", Toast.LENGTH_SHORT).show();

            mRunningProcessCount -= processCount;

            mFreeMemory += freeMemory;

            intiMemoryUI();
            initProcessUI();
        }

        mAdapter.notifyDataSetChanged();

    }


    private class ProcessAdapter extends BaseAdapter implements StickyListHeadersAdapter{

        @Override
        public int getCount() {

            if (showSystem) {
                if (mDatas != null) {
                    return mDatas.size();
                }
            } else {
                if (mUserDatas != null) {
                    return mUserDatas.size();
                }
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {

            if (showSystem) {
                if (mDatas != null) {
                    return mDatas.get(position);
                }
            } else {
                if (mUserDatas != null) {
                    return mUserDatas.get(position);
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
                convertView = View.inflate(getApplicationContext(), R.layout.item_process, null);
                holder = new ViewHolder();
                convertView.setTag(holder);

                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_process_iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_process_tv_name);
                holder.tvMemory = (TextView) convertView.findViewById(R.id.item_process_tv_memory);
                holder.cbChoice = (CheckBox) convertView.findViewById(R.id.item_process_cb_choice);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //set data
            ProcessInfo info = null;

            if (showSystem) {
                info = mDatas.get(position);
            } else {
                info = mUserDatas.get(position);
            }

            holder.ivIcon.setImageDrawable(info.icon);
            holder.tvName.setText(info.name);
            holder.tvMemory.setText("占用内存：" + Formatter.formatFileSize(getApplicationContext(), info.memory));

            holder.cbChoice.setChecked(info.checked);

            if (info.packageName.equals(getPackageName())) {
                holder.cbChoice.setVisibility(View.GONE);
            } else {
                holder.cbChoice.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                convertView = new TextView(getApplicationContext());
                tv = (TextView) convertView;

                tv.setPadding(4, 4, 4, 4);
                tv.setBackgroundColor(Color.parseColor("#33000000"));
                tv.setTextSize(15);
                tv.setTextColor(Color.BLACK);
            } else {
                tv = (TextView) convertView;
            }

            ProcessInfo info = null;
            if (showSystem) {
                info = mDatas.get(position);
            } else {
                info = mUserDatas.get(position);
            }
            boolean isSystem = info.isSystem;

            tv.setText(isSystem ? "系统进程（" + mSystemDatas.size() + "个）" : "用户进程（" + mUserDatas.size() + "个）");

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            ProcessInfo info = null;
            if (showSystem) {
                info = mDatas.get(position);
            } else {
                info = mUserDatas.get(position);
            }

            return info.isSystem ? 0 : 1;
        }
    }


    private class ViewHolder {
        ImageView ivIcon;
        TextView tvName;
        TextView tvMemory;
        CheckBox cbChoice;
    }
}
