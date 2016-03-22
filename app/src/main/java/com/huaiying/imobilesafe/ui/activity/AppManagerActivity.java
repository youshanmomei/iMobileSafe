package com.huaiying.imobilesafe.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.AppInfo;
import com.huaiying.imobilesafe.engine.AppInfoProvider;
import com.huaiying.imobilesafe.ui.view.ProgressDesView;
import com.huaiying.imobilesafe.util.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AppManagerActivity extends AppCompatActivity {

    public static final String TAG = "AppManagerActivity";

    private ProgressDesView mPdvRom;
    private ProgressDesView mPdvSD;
    private ListView mListView;
    private LinearLayout mLlLoding;
    private TextView mTvHeader;

    private List<AppInfo> mUserDatas;
    private AppAdapter mAdapter;
    private List<AppInfo> mSystemDatas;
    private List<AppInfo> mDatas;

    private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d(TAG, "接收到卸载广播");
            String dataString = intent.getDataString();
            Logger.d(TAG, "卸载了：" + dataString);
            String packageName = dataString.replace("package:", "");

            //update UI
            ListIterator<AppInfo> iterator = mUserDatas.listIterator();
            while (iterator.hasNext()) {
                AppInfo next = iterator.next();
                if (next.packageName.equals(packageName)) {
                    //remove
                    iterator.remove();
                    break;
                }
            }

            // update adapter
            mAdapter.notifyDataSetChanged();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        //init view
        mPdvRom = (ProgressDesView) findViewById(R.id.am_pdv_rom);
        mPdvSD = (ProgressDesView) findViewById(R.id.am_pdv_sd);
        mListView = (ListView) findViewById(R.id.am_listview);
        mLlLoding = (LinearLayout) findViewById(R.id.css_loading);
        mTvHeader = (TextView) findViewById(R.id.am_tv_header);

        //register the broadcast of the install and uninstall package
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mPackageReceiver, filter);

        //set data
        //set data to internal memory
        File dataDirectory = Environment.getDataDirectory();
        long romFreeSpace = dataDirectory.getFreeSpace();//剩余空间
        long romTotalSpace = dataDirectory.getTotalSpace();//总间
        long romUsedSpace = romTotalSpace - romFreeSpace;//已经使用的

        //left: has been used
        mPdvRom.setDesTitle("内存:");
        mPdvRom.setDesLeft(Formatter.formatFileSize(this, romUsedSpace) + "已用");
        mPdvRom.setDesRight(Formatter.formatFileSize(this, romFreeSpace) + "可用");
        int romProgress = (int) (romUsedSpace * 100f / romTotalSpace + 0.5f);
        mPdvRom.setDesProgress(romProgress);

        //2.set data to SD card
        File sdDirctory = Environment.getExternalStorageDirectory();
        long sdFreeSpace = sdDirctory.getFreeSpace();
        long sdTotalSpace = sdDirctory.getTotalSpace();
        long sdUsedSpace = sdTotalSpace - sdFreeSpace;

        mPdvSD.setDesTitle("SD卡:");
        mPdvSD.setDesLeft(Formatter.formatFileSize(this, sdUsedSpace) + "已用");
        mPdvSD.setDesRight(Formatter.formatFileSize(this, sdFreeSpace) + "可用");
        int sdProgress = (int) (romUsedSpace * 100f / romTotalSpace + 0.5f);
        mPdvSD.setDesProgress(sdProgress);

        //init data
        mLlLoding.setVisibility(View.VISIBLE);
        mTvHeader.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mDatas = AppInfoProvider.getAllApps(getApplicationContext());
                mSystemDatas = new ArrayList<AppInfo>();
                mUserDatas = new ArrayList<AppInfo>();

                for (AppInfo info : mDatas) {
                    if (info.isSystem) {
                        //system
                        mSystemDatas.add(info);
                    } else {
                        mUserDatas.add(info);
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLlLoding.setVisibility(View.GONE);
                        mTvHeader.setVisibility(View.VISIBLE);

                        //set data to header layout
                        mTvHeader.setText("用户程序（" + mUserDatas.size() + "个）");

                        //4.set data to listView
                        mAdapter = new AppAdapter();
                        mListView.setAdapter(mAdapter);
                    }
                });
            }
        }).start();

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserDatas == null || mSystemDatas == null) {
                    return;
                }

                int userSize = mUserDatas.size();

                //if a head is the first to be seen, it is displayed the corresponding header
                //如果 某个头是第一个可见,就显示为对应的头
                if (firstVisibleItem >= 0 && firstVisibleItem <= userSize) {
                    mTvHeader.setText("用户程序（" + mUserDatas.size() + "）个");
                } else if (firstVisibleItem >= userSize + 1) {
                    mTvHeader.setText("系统程序（" + mSystemDatas.size() + "）个");
                }
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            private PopupWindow mWindow;
            private View contentView;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //popup  弹出层
                //if click on the headline, don't respond
                if (position == 0) {
                    return;
                }

                int userSize = mUserDatas.size();
                if (position == userSize +1 ) {
                    return;
                }

                //get data
                AppInfo info = null;
                if (position > 0 && position < userSize + 1) {
                    info = mUserDatas.get(position - 1);
                } else {
                    info = mSystemDatas.get(position - userSize - 2);
                }

                //init popupwindow
                if(mWindow == null) {
                    contentView = View.inflate(getApplicationContext(), R.layout.popup_item_app, null);

                    //display view width and height
                    int width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;

                    mWindow = new PopupWindow(contentView, width, height);

                    //focus
                    mWindow.setFocusable(true);

                    //set other locations to touch
                    mWindow.setOutsideTouchable(true);
                    mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    //set Animotion
                    mWindow.setAnimationStyle(R.style.PopAnimation);
                }

                final AppInfo app = info;
                contentView.findViewById(R.id.pop_ll_uninstall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.DELETE");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + app.packageName));
                        startActivity(intent);

                        //popup
                        mWindow.dismiss();
                    }
                });

                contentView.findViewById(R.id.pop_ll_share).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("sms_body", "分享XX软件");
                        startActivity(smsIntent);

                        mWindow.dismiss();
                    }
                });

                contentView.findViewById(R.id.pop_ll_open).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PackageManager pm = getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(app.packageName);

                        if (intent != null) {
                            startActivity(intent);
                        }

                        mWindow.dismiss();
                    }
                });

                contentView.findViewById(R.id.pop_ll_info).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:" + app.packageName));
                        startActivity(intent);

                        mWindow.dismiss();
                    }
                });

                mWindow.showAsDropDown(view, 60, -view.getHeight());

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPackageReceiver);
    }

    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            int systemCount = 0;
            if (mSystemDatas != null) {
                systemCount = mSystemDatas.size();
                systemCount += 1;
            }

            int userCount = 0;
            if (mUserDatas != null) {
                userCount = mUserDatas.size();
                userCount += 1;
            }
            return systemCount + userCount;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int userSize = mUserDatas.size();

            //user part
            if (position == 0) {
                //display the user portion of the header
                TextView tv = new TextView(getApplicationContext());

                tv.setPadding(4, 4, 4, 4);
                tv.setBackgroundColor(Color.parseColor("#ffcccccc"));
                tv.setTextColor(Color.BLACK);

                tv.setText("用户程序（" + userSize + "）");
                return tv;
            }
            
            //system part
            int systemSize = mSystemDatas.size();
            if (position == userSize + 1) {
                TextView tv = new TextView(getApplicationContext());

                tv.setPadding(4,4,4,4);
                tv.setBackgroundColor(Color.parseColor("#ffcccccc"));
                tv.setTextColor(Color.BLACK);

                tv.setText("系统程序（" + systemSize + "）个");

                return tv;
            }

            ViewHolder holder = null;
            if (convertView == null || (convertView instanceof TextView)) {
                //no reuse
                //1. init view
                convertView = View.inflate(getApplicationContext(), R.layout.item_app_info, null);

                //2.init holder
                holder = new ViewHolder();

                //3.set flag
                convertView.setTag(holder);

                //4.initialize view holder
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.item_appinfo_iv_icon);
                holder.tvName = (TextView) convertView.findViewById(R.id.item_appinfo_tv_name);
                holder.tvInstallPath = (TextView) convertView.findViewById(R.id.item_appinfo_tv_install);
                holder.tvSize = (TextView) convertView.findViewById(R.id.item_appinfo_tv_size);
            }else {
                //reuse
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo info = null;
            if (position < userSize + 1) {
                //display the user program section
                info = mUserDatas.get(position - 1);
                Logger.d(TAG, "用户部分：" + (position-1));
            }else {
                //display the system program section
                info = mSystemDatas.get(position - userSize - 2);
                Logger.d(TAG, "系统部分：" + (position - userSize - 2));
            }

            holder.ivIcon.setImageDrawable(info.icon);
            holder.tvInstallPath.setText(info.isInstallSD ? "SD卡安装" : "手机内存");
            holder.tvName.setText(info.name);
            holder.tvSize.setText(Formatter.formatShortFileSize(getApplicationContext(), info.size));

            return convertView;
        }
    }

    private class ViewHolder{
        ImageView ivIcon;
        TextView tvName;
        TextView tvInstallPath;
        TextView tvSize;
    }
}
