package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huaiying.imobilesafe.BlackEditActivity;
import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.db.BlackDao;
import com.huaiying.imobilesafe.db.dao.BlackInfo;

import java.util.List;

public class CallSmsSafeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_UPDATE = 101;
    private static final int PAGE_SIZE = 10;
    private BlackDao mDao;
    private ImageView mIvAdd;
    private ListView mListView;
    private LinearLayout mLlLoading;
    private ImageView mIvEmpty;

    private List<BlackInfo> mDatas;
    private CallSmsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        mDao = new BlackDao(this);

        //init view
        mIvAdd = (ImageView) findViewById(R.id.css_iv_add);
        mListView = (ListView) findViewById(R.id.css_lv);
        mLlLoading = (LinearLayout) findViewById(R.id.css_loading);
        mIvEmpty = (ImageView) findViewById(R.id.css_iv_empty);

        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CallSmsSafeActivity.this, BlackEditActivity.class), REQUEST_CODE_ADD);
            }
        });

        //set click event for item listView
        mListView.setOnItemClickListener(this);
        
        startQuery();


    }

    private void startQuery() {
        mLlLoading.setVisibility(View.VISIBLE);

        //open thread to query data
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //access interface -> net
                // init List data
                //mDatas = mDao.findAll();
               mDatas = mDao.findPart(PAGE_SIZE, 0);

                //setting adapter in the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLlLoading.setVisibility(View.GONE);

                        //set data to listView
                        mAdapter = new CallSmsAdapter();
                        mListView.setAdapter(mAdapter);

                        //set empty view
                        mListView.setEmptyView(mIvEmpty);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BlackInfo info = mDatas.get(position);
        //jump to updata view
        Intent intent = new Intent(this, BlackEditActivity.class);
        intent.setAction(BlackEditActivity.ACTION_UPDATE);
        intent.putExtra(BlackEditActivity.EXTRA_NUMBEER, info.number);
        intent.putExtra(BlackEditActivity.EXTRA_TYPE, info.type);
        intent.putExtra(BlackEditActivity.EXTRA_POSITION, position);
        startActivityForResult(intent, REQUEST_CODE_UPDATE);
    }

    private class CallSmsAdapter extends BaseAdapter {

        private ViewHolder holder;

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

            ViewHolder holder = null;
            if (convertView == null) {
                //no reuse
                //1.load view
                convertView = View.inflate(CallSmsSafeActivity.this, R.layout.item_black, null);
                //2.init holder
                holder = new ViewHolder();
                //3.bind holder, make flag
                convertView.setTag(holder);
                //4.initialization of View in holder
                holder.tvNumber = (TextView) convertView.findViewById(R.id.item_black_tv_number);
                holder.tvType = (TextView) convertView.findViewById(R.id.item_black_tv_type);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.item_black_iv_delete);
            } else {
                //reuse
                holder = (ViewHolder) convertView.getTag();
            }

            final BlackInfo info = mDatas.get(position);
            //set data to view
            holder.tvNumber.setText(info.number);

            switch (info.type) {
                case BlackInfo.TYPE_CALL:
                    holder.tvType.setText("电话拦截");
                    break;
                case BlackInfo.TYPE_SMS:
                    holder.tvType.setText("短信拦截");
                    break;
                case BlackInfo.TYPE_ALL:
                    holder.tvType.setText("电话+短信拦截");
                    break;
                default:
                    break;
            }

            //set delete click event
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //delete database
                    boolean delete = mDao.delete(info.number);
                    if (delete) {
                        mDatas.remove(info);

                        //updata ui
                        mAdapter.notifyDataSetChanged();
                        ;

                        Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();

                        startQuery(1, mDatas.size());
                    } else {
                        Toast.makeText(getApplicationContext(), "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }

        class ViewHolder{
            TextView tvNumber;
            TextView tvType;
            ImageView ivDelete;
        }
    }

    private void startQuery(final int pageSize, final int offset) {
        mLlLoading.setVisibility(View.VISIBLE);

        //open thread to query data
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //access interface -> net
                // init List data
                //mDatas = mDao.findAll();
                mDatas = mDao.findPart(pageSize, offset);

                //setting adapter in the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLlLoading.setVisibility(View.GONE);

                        //set data to listView
                        mAdapter = new CallSmsAdapter();
                        mListView.setAdapter(mAdapter);

                        //set empty view
                        mListView.setEmptyView(mIvEmpty);
                    }
                });
            }
        }.start();
    }
}
