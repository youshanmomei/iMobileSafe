package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.ProcessInfo;
import com.huaiying.imobilesafe.engine.ProcessProvider;
import com.huaiying.imobilesafe.service.AutoCleanService;
import com.huaiying.imobilesafe.ui.view.ProgressDesView;
import com.huaiying.imobilesafe.ui.view.SettingItemView;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.ServiceStateUtils;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class ProcessManagerActivity extends AppCompatActivity {

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
        ProcessProvider.getRunningProgressCount(this);
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

//    ...

    private class ProcessAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}
