package com.huaiying.imobilesafe.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;

/**
 * Created by admin on 2016/3/23.
 */
public class SettingItemView extends RelativeLayout {

    private String TAG = "SettingItemView";
    private final static int BKG_FIRST = 0;
    private final static int BKG_MIDDLE = 1;
    private final static int BKG_LAST = 2;
    private boolean isToggleEnable = true;


    private boolean isToggleOn;
    private boolean toggle;
    private TextView mTvTitle;
    private ImageView mIvToggle;


    public SettingItemView(Context context) {
        super(context, null);
    }

    public SettingItemView(Context context, AttributeSet set) {
        super(context, set);

        //binding view and layout
        View view = View.inflate(context, R.layout.view_setting_item, this);

        mTvTitle = (TextView) findViewById(R.id.view_pdv_tv_title);
        mIvToggle = (ImageView) findViewById(R.id.view_iv_toggle);

        //read the custom attribute
        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.SettingItemView);

        String title = ta.getString(R.styleable.SettingItemView_settingItemTitle);
        int bkg = ta.getInt(R.styleable.SettingItemView_itbackground, 0);
        isToggleEnable = ta.getBoolean(R.styleable.SettingItemView_toggleEnable, isToggleEnable);

        //recovery
        ta.recycle();

        mTvTitle.setText(title);

        //set background
        switch (bkg) {
            case BKG_FIRST:
                view.setBackgroundResource(R.drawable.setting_first_selector);
                break;
            case BKG_MIDDLE:
                view.setBackgroundResource(R.drawable.setting_middle_selector);
                break;
            case BKG_LAST:
                view.setBackgroundResource(R.drawable.setting_last_selector);
                break;
            default:
                view.setBackgroundResource(R.drawable.setting_first_selector);
                break;
        }

        //set default value
        setToggleOn(isToggleOn);

        mIvToggle.setVisibility(isToggleEnable?View.VISIBLE:View.GONE);
    }

    public void setToggleOn(boolean on) {
        this.isToggleOn = on;
        if (on) {
            mIvToggle.setImageResource(R.mipmap.on);
        } else {
            mIvToggle.setImageResource(R.mipmap.off);
        }
    }

    //stop it if state open, open it if state close
    public void toggle(){
        setToggleOn(!isToggleOn);
    }

    public boolean isToggleOn(){
        return isToggleOn();
    }
}
