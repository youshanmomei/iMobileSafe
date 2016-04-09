package com.huaiying.imobilesafe.ui.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huaiying.imobilesafe.R;
import com.huaiying.imobilesafe.util.Constants;
import com.huaiying.imobilesafe.util.Logger;
import com.huaiying.imobilesafe.util.SharePreferenceUtils;

/**
 * Created by admin on 2016/4/9.
 */
public class AddressToast implements View.OnTouchListener {

    private static final String TAG = "AddressToast";
    private Context mContext;
    private WindowManager mWM;
    private final WindowManager.LayoutParams mParams;
    private View mView;

    private float startX;
    private float startY;


    public AddressToast(Context context) {
        this.mContext = context;

        WindowManager mWM = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mParams.flags = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
    }

    public void show(String address) {
        hide();

        mView = View.inflate(mContext, R.layout.toast_address, null);
        int style = SharePreferenceUtils.getInt(mContext, Constants.ADDRESS_STYLE, R.drawable.toast_address_normal);
        mView.setBackgroundResource(style);

        TextView tv = (TextView) mView.findViewById(R.id.toast_tv_address);
        tv.setText(address);

        mView.setOnTouchListener(this);

        mWM.addView(mView, mParams);

    }

    public void hide() {
        if (mView != null) {
            if (mView.getParent() != null) {
                mWM.removeView(mView);
            }

            mView = null;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.d(TAG, "按下");
                startX = event.getRawX();
                startY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.d(TAG, "移动");
                float newX = event.getRawX();
                float newY = event.getRawY();

                float diffX = newX - startX;
                float diffY = newY - startY;

                mParams.x += (int) (diffX + 0.5f);
                mParams.y += (int) (diffY + 0.5f);

                if (mView != null) {
                    mWM.updateViewLayout(mView, mParams);
                }

                startX = newX;
                startY = newY;
                break;
            case MotionEvent.ACTION_UP:
                Logger.d(TAG, "抬起");
                break;
            default:
                break;

        }
        return true;
    }
}
