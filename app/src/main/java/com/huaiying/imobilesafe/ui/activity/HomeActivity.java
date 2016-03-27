package com.huaiying.imobilesafe.ui.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.HomeItem;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "HomeActivity";

    private final static String[] TITLES = new String[]{
            "手机防盗", "骚扰拦截", "软件管家"
            , "进程管理", "手机杀毒", "常用工具"
    };

    private final static String[] DESCS = new String[]{
            "远程定位手机", "全面拦截骚扰", "管理您的软件"
            , "管理运行进程", "病毒无处藏身", "工具大全"
    };

    private final static int[] ICONS = new int[]{
            R.mipmap.sjfd, R.mipmap.srlj, R.mipmap.rjgj,
            R.mipmap.jcgl, R.mipmap.sjsd, R.mipmap.cygj
    };
    private ImageView mIvLogo;
    private GridView mGridView;
    private List<HomeItem> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //view的初始化
        mIvLogo = (ImageView) findViewById(R.id.home_iv_logo);
        mGridView = (GridView) findViewById(R.id.home_gridview);

        //给ImageView做动画
        //mIvLogo.setRotationY(rotationY)
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY",
                0, 90, 279, 360);
        animator.setDuration(2000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();

        //list数据的初始化
        mDatas = new ArrayList<>();
        for (int i = 0; i < ICONS.length; i++) {
            HomeItem item = new HomeItem();
            item.iconId = ICONS[i];
            item.title = TITLES[i];
            item.desc = DESCS[i];

            //添加
            mDatas.add(item);

        }

        mGridView.setAdapter(new HomeAdapter());

        //设置item的点击事件
        mGridView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                //手机防盗 TODO GPSService, SmsProvider, SmsReceiver, SafeAdmnReceiver
                performSjfd();
                break;
            case 1:
                //骚扰拦截 隐式启动方式
                performCallSmsSafe();
                break;
            case 2:
                //软件管理
                performAppManager();
                break;
            case 3:
                // 进程管理
                performProcessManager();
                break;
            case 4:
                //TODO 手机杀毒
                performAntiVirus();
                break;
            case 5:
                //TODO 常用工具
                performCommonTool();
                break;
            default:
                break;
        }
    }

    private void performCommonTool() {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }

    private void performAntiVirus() {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
    }

    private void performProcessManager() {
        startActivity(new Intent(HomeActivity.this, ProcessManagerActivity.class));
    }

    private void performAppManager() {
        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
    }

    private void performCallSmsSafe() {
        startActivity(new Intent(HomeActivity.this, CallSmsSafeActivity.class));
    }

    private void performSjfd() {
        String pwd = SharePreferenceUtils.getString(this, Constants.SJFD_PASSWORD);

        if (TextUtils.isEmpty(pwd)) {
            //如果是第一次进入，弹出设置密码的对话框
            Logger.d(TAG, "弹出设置密码的对话框");
            showInitPwdDialog();
        } else {
            //否则，弹出输入密码的对话框
            Logger.d(TAG, "弹出输入密码的对话框");
            showEnterPwdDialog();
        }

    }

    private void showEnterPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_pwd_enter, null);
        final EditText etPwd = (EditText) view.findViewById(R.id.dialog_tv_pwd);
        Button  btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        //设置点击事件
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //非空判断
                String pwd = etPwd.getText().toString().trim();

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    etPwd.requestFocus(); //获取焦点
                    return;
                }

                //校验密码是否正确
                String local = SharePreferenceUtils.getString(HomeActivity.this, Constants.SJFD_PASSWORD);
                if (local.equals(pwd)) {
                    //正确--》进入手机防盗界面
                    enterSjfd();
                    dialog.dismiss();
                }else{
                    Toast.makeText(HomeActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void showInitPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_pwd_init, null);
        final EditText etPwd = (EditText) view.findViewById(R.id.dialog_tv_pwd);
        final EditText etConfirm = (EditText) view.findViewById(R.id.dialog_tv_confirm);
        Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        //设置点击事件
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //非空的校验
                String pwd = etPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    etPwd.requestFocus(); //获取焦点
                    return;
                }

                String confirm = etConfirm.getText().toString().trim();
                if (TextUtils.isEmpty(confirm)) {
                    Toast.makeText(HomeActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT).show();
                    etConfirm.requestFocus(); //获取焦点
                    return;
                }

                //判断两次是否相同
                if (!pwd.equals(confirm)) {
                    Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                //保存密码
                SharePreferenceUtils.putString(HomeActivity.this, Constants.SJFD_PASSWORD, pwd);

                enterSjfd();

                //关闭dialog
                dialog.dismiss();;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭dialog
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void enterSjfd(){
        Logger.d(TAG, "进入手机防盗的页面");

        boolean flag = SharePreferenceUtils.getBoolean(this, Constants.SJFD_SETUP);

        //1.引导页面
        //2.最终页面
        if (flag) {
            //如果用户开启了 放到保护 --》最终页面
            Logger.d(TAG, "进入最终页面");
            startActivity(new Intent(this, LostFindActivity.class));
        }else{
            // 否则进入引导界面
            Logger.d(TAG, "进入引导界面");
            startActivity(new Intent(this, LostSetup1Activity.class));
        }

    }

    private class HomeAdapter extends BaseAdapter {

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
            View view = View.inflate(HomeActivity.this, R.layout.item_home, null);

            ImageView ivIcon = (ImageView) view.findViewById(R.id.item_home_iv_icon);
            TextView tvTitle = (TextView) view.findViewById(R.id.item_home_tv_title);
            TextView tvDesc = (TextView) view.findViewById(R.id.item_home_tv_desc);

            HomeItem item = mDatas.get(position);
            ivIcon.setImageResource(item.iconId);
            tvTitle.setText(item.title);
            tvDesc.setText(item.desc);

            return view;
        }
    }
}