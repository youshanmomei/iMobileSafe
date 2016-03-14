package com.huaiying.imobilesafe.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.bean.ContactInfo;
import com.huaiying.imobilesafe.util.ContactUtils;

public class ContactSelectedActivity2 extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final String KEY_NUMBER = "number";
    private ListView mListView;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_selected2);

        mListView = (ListView) findViewById(R.id.cs_listview);
        final ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.cs_pb);

        //execute in sub-thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //load data
                //mDatas = ContactUtils.getAllPhone(ContactSelectActivity2.this);

                mCursor = ContactUtils.getAllPhoneCursor(ContactSelectedActivity2.this);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //sub-thread can not operate ui
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //hidden progressBar
                        mProgressBar.setVisibility(View.GONE);

                        //set data to listView
                        mListView.setAdapter(new ContactAdapter(ContactSelectedActivity2.this, mCursor));
                    }
                });

            }
        }).start();

        //set onclickListener
        mListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mCursor.moveToPosition(position);
        ContactInfo info = ContactUtils.getContactInfo(mCursor);

        Intent data = new Intent();
        data.putExtra(KEY_NUMBER, info.number);
        setResult(Activity.RESULT_OK, data);

        finish();
    }

    private class ContactAdapter extends CursorAdapter {

        public ContactAdapter(Context context, Cursor c) {
            super(context, c);
        }

        public ContactAdapter(Context context, Cursor c, boolean autoRequery) {
            super(context, c, autoRequery);
        }

        public ContactAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            //create view
            return View.inflate(ContactSelectedActivity2.this, R.layout.item_contact, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //bind data
            // view : display view
            ImageView ivIcon = (ImageView) view.findViewById(R.id.item_contact_iv_icon);
            TextView tvName = (TextView) view.findViewById(R.id.item_contact_tv_name);
            TextView tvNumber = (TextView) view.findViewById(R.id.item_contact_tv_number);

            //set data
            //cursor : data
            ContactInfo info = ContactUtils.getContactInfo(cursor);

            tvName.setText(info.name);
            tvNumber.setText(info.number);

            Bitmap bitmap = ContactUtils.getContactIcon(ContactSelectedActivity2.this, info.contactId);
            if (bitmap != null) {
                ivIcon.setImageBitmap(bitmap);
            } else {
                ivIcon.setImageResource(R.mipmap.ic_contact);
            }

        }
    }
}
