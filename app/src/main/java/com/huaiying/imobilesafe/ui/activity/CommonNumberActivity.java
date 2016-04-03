package com.huaiying.imobilesafe.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.db.CommonNumberDao;

public class CommonNumberActivity extends AppCompatActivity {

    private ExpandableListView mListView;
    private int mCurrentOpenPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_number);

        mListView = (ExpandableListView) findViewById(R.id.cn_listView);
        mListView.setAdapter(new CommonNumberAdapter());

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mCurrentOpenPosition == -1) {
                    mListView.expandGroup(groupPosition);
                    mCurrentOpenPosition = groupPosition;
                    mListView.setSelectedGroup(groupPosition);
                } else {
                    if (mCurrentOpenPosition == groupPosition) {
                        mListView.collapseGroup(groupPosition);
                        mCurrentOpenPosition = -1;
                    } else {
                        //if checked is not open
                        mListView.collapseGroup(mCurrentOpenPosition);
                        mListView.expandGroup(groupPosition);
                        mListView.setSelectedGroup(groupPosition);
                        mCurrentOpenPosition = groupPosition;
                    }
                }
                return true;
            }
        });

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String[] text = CommonNumberDao.getChildText(getApplicationContext(), groupPosition, childPosition);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + text[1]));
                startActivity(intent);
                return true;
            }
        });

    }

    private class CommonNumberAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return CommonNumberDao.getGroupCount(getApplicationContext());
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return CommonNumberDao.getChildCount(getApplicationContext(), groupPosition);
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            TextView tv = null;
            if (convertView == null) {
                convertView = new TextView(getApplicationContext());
                tv = (TextView) convertView;
                tv.setBackgroundColor(Color.parseColor("#33000000"));
                tv.setTextSize(18);
                tv.setPadding(10, 10, 10, 10);
                tv.setTextColor(Color.BLACK);
            } else {
                tv = (TextView) convertView;
            }

            tv.setText(CommonNumberDao.getGroupText(getApplicationContext(), groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                convertView = new TextView(getApplicationContext());

                tv = (TextView) convertView;
                tv.setTextSize(16);
                tv.setPadding(8, 8, 8, 8);
                tv.setTextColor(Color.BLACK);
            } else {
                tv = (TextView) convertView;
            }

            String[] texts = CommonNumberDao.getChildText(getApplicationContext(), groupPosition, childPosition);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < texts.length; i++) {
                sb.append(texts[i]);
                if (i != (texts.length - 1)) {
                    sb.append("\n");
                }
            }
            tv.setText(sb.toString());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
