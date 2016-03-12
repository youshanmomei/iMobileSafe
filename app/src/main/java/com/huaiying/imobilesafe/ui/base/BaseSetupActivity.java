package com.huaiying.imobilesafe.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.huaiying.imobilesafe.R;

/**
 * Created by admin on 2016/3/12.
 */
public abstract class BaseSetupActivity extends Activity{

    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //create instance
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return super.onFling(e1, e2, velocityX, velocityY);

                // e1: MotionEvent, Event data carrier, down
                // e2: move
                // velocityX: x direction rate

                float x1 = e1.getRawX();
                float x2 = e2.getRawX();

                float y1 = e1.getRawY();
                float y2 = e1.getRawY();

                if (Math.abs(y1 - y2) > Math.abs(x1 - x2)) {
                    // y state movement
                    return false;
                }

                // from right to left : x1>x2
                if (x1 > x2 + 50) {
                    //if you side from right to left, do to the next page
                    //Toast.makeText(...).show();
                    doNext();

                    //handle current touch
                    return true;
                }

                if (x1 + 50 < x2) {
                    //if you side from left to right, do to the next page
                    doPre();
                    
                    //handle current touch
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public void clickPre(View view){
        doPre();
    }

    public void clickNext(View view){
        doNext();
    }

    private void doPre() {
        if (performPre()) {
            return;
        }

        overridePendingTransition(R.anim.pre_ebnter, R.anim.pre_exit);
    }

    private void doNext() {
        //page jump --- the result is different
        if (performNext()) {
            return;
        }

        //animation operation --- the same
        overridePendingTransition(R.anim.next_enter, R.anim.next_exit);

        //the same
        finish();
    }


    /**
     * let the child go to the pre step of the opration
     * @return
     */
    protected abstract boolean performPre();

    /**
     * let the child go to the next step of the operation
     * @return if the returned is true, it will not continue down
     */
    protected abstract boolean performNext();
}
