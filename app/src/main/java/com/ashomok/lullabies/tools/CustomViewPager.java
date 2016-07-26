package com.ashomok.lullabies.tools;

import android.content.Context;
import android.nfc.Tag;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by iuliia on 7/26/16.
 */
public class CustomViewPager extends ViewPager {

    private static final String TAG = CustomViewPager.class.getSimpleName();
    float mStartDragX;
    OnSwipeOutListener mOnSwipeOutListener;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mOnSwipeOutListener = listener;
    }

    private void onSwipeOutAtStart() {
        if (mOnSwipeOutListener != null) {
            mOnSwipeOutListener.onSwipeOutAtStart();
            Log.d(TAG, "onSwipeOutAtStart()");
        }
    }

    private void onSwipeOutAtEnd() {
        if (mOnSwipeOutListener != null) {
            mOnSwipeOutListener.onSwipeOutAtEnd();
            Log.d(TAG, "onSwipeOutAtEnd()");
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartDragX = ev.getX();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (getCurrentItem() == 0 || getCurrentItem() == getAdapter().getCount() - 1) {
            final int action = ev.getAction();
            float x = ev.getX();
            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (getCurrentItem() == 0 && x > mStartDragX) {
                        onSwipeOutAtStart();
                    }
                    if (getCurrentItem() == getAdapter().getCount() - 1 && x < mStartDragX) {
                        onSwipeOutAtEnd();
                    }
                    break;
            }
        } else {
            mStartDragX = 0;
        }
        return super.onTouchEvent(ev);

    }

    public interface OnSwipeOutListener {
        void onSwipeOutAtStart();
        void onSwipeOutAtEnd();
    }

}
