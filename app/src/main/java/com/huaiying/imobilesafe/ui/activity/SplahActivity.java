package com.huaiying.imobilesafe.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.service.ProtectedService;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.GZipUtils;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.PackageUtils;
import com.huaiying.imobilesafe.util.ServiceStateUtils;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplahActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private TextView mTvVersion;
    /** 版本更新的描述信息 **/
    private String mDesc;
    /** 最新版本的url **/
    private String mUrl;

    /** 自己发送的请求代码 **/
    protected static final int REQUEST_CODE_INSALL = 100;
    /** 展示更新日志 **/
    public static final int SHOW_UPDATE_DIALOG = 120;
    /** 展示错误 **/
    public static final int SHOW_ERROR = 110;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;

            switch (what) {
                case SHOW_ERROR:
                    //提示错误：
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    //进入主页
                    load2Home();
                    break;
                case SHOW_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah);

        //初始化view
        mTvVersion = (TextView) findViewById(R.id.splash_tv_version);

        //显示版本号
        mTvVersion.setText(PackageUtils.getVersionName(this));

        //检查版本是否更新
        boolean update = SharePreferenceUtils.getBoolean(this, Constants.AUTO_UPDATE, true);
        if (update) {
            Logger.d(TAG, "需要检测更新");
            checkVersionUpdate();
        } else {
            Logger.d(TAG, "不需要检测更新");
            load2Home();
        }

        //拷贝解压号码归属地数据库
        copyAddressDB();

        //拷贝常用号码
        copyCommonNumberDB();

        //拷贝病毒数据库
        copyVirusDB();

        //开启必要的服务
        if(!ServiceStateUtils.isRunging(this, ProtectedService.class)) {
            startService(new Intent(this, ProtectedService.class));
        }

        boolean flag = SharePreferenceUtils.getBoolean(this, Constants.HAS_SHORTCUT);
        if(!flag){
            //创建快捷图标
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

            Intent intent = new Intent();
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            //指定名称
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "小Q卫士");
            //指定图标
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
            //指定行为
            Intent clickIntent = new Intent(this, SplahActivity.class);
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, clickIntent);

            sendBroadcast(intent);

            //设置
            SharePreferenceUtils.putBoolean(this, Constants.HAS_SHORTCUT, true);

        }

    }

    private void copyVirusDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File destFile = new File(getFilesDir(), "antivirus.db");
                if (!destFile.exists()) {
                    Logger.d(TAG,"antivirus.db数据库不存在，需要拷贝");
                    //拷贝
                    AssetManager assets = getAssets();
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = assets.open("antivirus.db");
                        fos = new FileOutputStream(destFile);

                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        close(is);
                        close(fos);
                    }
                }else{
                    Logger.d(TAG,"antivirus.db数据库存在，不需要拷贝");
                }

            }
        }).start();
    }

    private void copyCommonNumberDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File destFile = new File(getFilesDir(), "commonnum.db");
                if (!destFile.exists()) {
                    Logger.d(TAG, "commonnum数据库不存在需要拷贝");
                    //拷贝
                    AssetManager assets = getAssets();
                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = assets.open("commonnum.db");
                        fos = new FileOutputStream(destFile);

                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        close(is);
                        close(fos);
                    }
                } else {
                    Logger.d(TAG, "commonnum数据库已存在不需要拷贝");
                }
            }
        }).start();
    }

    private void copyAddressDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dessFile = new File(getFilesDir(), "address.db");
                if (!dessFile.exists()) {
                    Logger.d(TAG, "数据库不存在，需要拷贝");
                    copyAndunZipAddressDB2();
                } else {
                    Logger.d(TAG, "数据库已经存在，不需要拷贝");
                }

            }

            private void copyAndunZipAddressDB2(){
                AssetManager assets = getAssets();
                InputStream is = null;
                FileOutputStream os = null;
                File destFile = new File(getFilesDir(), "address.db");
                try {
                    is = assets.open("address.zip");
                    os = new FileOutputStream(destFile);
                    GZipUtils.unzip(is, os);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            private void copyAndunZipAddressDB1(){
                //1.拷贝文件
                AssetManager assets = getAssets();
                File destFile = new File(getFilesDir(), "address.zip");

                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    is = assets.open("address.zip");
                    fos = new FileOutputStream(destFile);

                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while((len = is.read(buffer)) != -1){
                        fos.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(is);
                    close(fos);
                }

                //2.解压文件
                File zipFile = new File(getFilesDir(), "address.zip");
                File targetFile = new File(getFilesDir(), "address.db");
                try {
                    GZipUtils.unzip(zipFile, targetFile);

                    //删除zip文件
                    zipFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            io = null;
        }
    }

    private void load2Home(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String name = Thread.currentThread().getName();
                Logger.d(TAG, "name:" + name);

                Intent intent = new Intent(SplahActivity.this, HomeActivity.class);
                startActivity(intent);

                finish();
            }
        }, 1200);
    }

    /**
     * 获取版本信息
     */
    private void checkVersionUpdate() {
        new Thread(new CheckVersionTask()).start();
    }

    private void showUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //设置点击取消不可用
        builder.setCancelable(false);

        //设置title
        builder.setTitle("版本更新提醒");
        //设置文本
        builder.setMessage(mDesc);

        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //去下载最新的版本
                showProgressDialog();
            }
        });

        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                load2Home();
            }
        });

        //显示
        builder.show();

    }

    /**
     * 显示下载进度对话框
     */
    private void showProgressDialog() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgress(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        //下载新版本
        final String target = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+".apk").getAbsolutePath();
        String url = mUrl;
        final OkHttpClient mOkHttpClient = new OkHttpClient();
        //对于超过1MB的响应body，应该使用流的方式来处理body
        try {
            Request request = new Request.Builder().url(mUrl).build();
            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.d(TAG, "下载失败");
                    dialog.dismiss();

                    //进入主页
                    load2Home();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    dialog.setMax(100);
                    for (int i=0; i<100; i++){
                        dialog.setProgress(i);
                    }

                    Logger.d(TAG, "下载成功");
                    dialog.dismiss();

                    //去安装
                    //安装是系统行为
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setDataAndType(Uri.parse("file:" + target), "application/vnd.android.package-archive");

                    startActivityForResult(intent, REQUEST_CODE_INSALL);
                }


            });

        }catch (Exception e){
            Logger.d(TAG, "下载文件出现异常");
        }

    }

    private class CheckVersionTask implements Runnable{

        @Override
        public void run() {
            //服务器必须提供网络接口
            String uri = "http://192.168.10.1:8080/update.txt";

            //获取网络访问的客户端
            //创建okHttp对象
            final OkHttpClient mOkHttpClient = new OkHttpClient();
            //创建一个Request
            final Request request = new Request.Builder().url(uri).build();
            //创建一个请求call
            Call call = mOkHttpClient.newCall(request);
            //请求加入调度
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.d(TAG, "请求版本号失败");
                    //访问失败
                    Message msg = Message.obtain();
                    msg.what = SHOW_ERROR;
                    msg.obj = "code:130";
                    mHandler.sendMessage(msg);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result = response.body().string();
                    Logger.d(TAG, "访问结果：" + result);
                    // {versionCode:2}

                    // 获取本地版本号
                    int localCode = PackageUtils.getVersionCode(getApplicationContext());

                    //解析json
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);
                        int netCode = jsonObject.getInt("versionCode");

                        //比对
                        if (localCode < netCode) {
                            //需要更新
                            Logger.d(TAG, "需要更新");

                            mDesc = jsonObject.getString("desc");
                            mUrl = jsonObject.getString("url");

                            Message msg = Message.obtain();
                            msg.what = SHOW_UPDATE_DIALOG;
                            mHandler.sendMessage(msg);
                        }else{
                            //不需要更新
                            //进入主页
                            Logger.d(TAG,"不需要更新");
                            load2Home();
                        }

                    } catch (JSONException e) {
                        Message msg = Message.obtain();
                        msg.what = SHOW_ERROR;
                        msg.obj = "code: error";
                        mHandler.sendMessage(msg);
                    }

                }
            });


        }
    }
}
