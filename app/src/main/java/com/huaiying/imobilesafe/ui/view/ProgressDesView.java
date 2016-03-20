package com.huaiying.imobilesafe.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;

/**
 * Created by admin on 2016/3/20.
 */
public class ProgressDesView extends LinearLayout {

    private TextView mTvTitle;
    private TextView mTvLeft;
    private TextView mTvRight;
    private ProgressBar mPbProgress;

    public ProgressDesView(Context context) {
        super(context, null);
    }

    public ProgressDesView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //bind class and xml
        View.inflate(context, R.layout.view_progress_des, this);

        //init view
        mTvTitle = (TextView) findViewById(R.id.view_pdv_tv_right);
        mTvLeft = (TextView) findViewById(R.id.view_pdv_tv_left);
        mTvRight = (TextView) findViewById(R.id.view_pdv_tv_right);
        mPbProgress = (ProgressBar) findViewById(R.id.view_pdv_pb_progress);
    }

    public void setDesTitle(String title){
        mTvTitle.setText(title);
    }

    public void setDesLeft(String left) {
        mTvLeft.setText(left);
    }

    public void setDesRight(String right) {
        mTvRight.setText(right);
    }

    public void setDesProgress(int progress) {
        mPbProgress.setProgress(progress);
    }

}
