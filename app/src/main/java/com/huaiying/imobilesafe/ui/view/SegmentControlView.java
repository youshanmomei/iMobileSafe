package com.huaiying.imobilesafe.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;

/**
 * Created by admin on 2016/4/5.
 */
public class SegmentControlView extends LinearLayout implements View.OnClickListener {

    private TextView mTvLeft;
    private TextView mTvRight;
    private boolean isLeftSelected = true;

    private OnSelectedListener mListener;

    public SegmentControlView(Context context, AttributeSet attrs) {
        super(context, null);
    }

    public SegmentControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        //binding xml
        View.inflate(context, R.layout.view_segement, this);
        mTvLeft = (TextView) findViewById(R.id.view_pdv_tv_left);
        mTvRight = (TextView) findViewById(R.id.view_pdv_tv_right);

        //let the left side by default
        mTvLeft.setSelected(true);

        mTvLeft.setOnClickListener(this);
        mTvRight.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == mTvLeft) {
            if (!isLeftSelected) {
                mTvRight.setSelected(false);
                mTvLeft.setSelected(true);

                isLeftSelected = true;
                if (mListener != null) {
                    mListener.onSaelected(true);
                }
            }
        }else if (v == mTvRight) {
            if (isLeftSelected) {
                //if the right side is not selected, click on the right side of the check
                mTvRight.setSelected(true);
                mTvLeft.setSelected(false);

                isLeftSelected = false;
                if (mListener != null) {
                    mListener.onSaelected(false);
                }
            }
        }
    }

    public void setOnSelectedListener(OnSelectedListener listener){
        this.mListener = listener;
    }

    public interface OnSelectedListener{
        //selected and not selected
        void onSaelected(boolean isLeftSelected);
    }
}
