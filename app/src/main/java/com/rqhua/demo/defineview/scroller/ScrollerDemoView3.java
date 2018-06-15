package com.rqhua.demo.defineview.scroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.rqhua.demo.defineview.R;

/**
 * Created by Administrator on 2018/6/13.
 */

public class ScrollerDemoView3 extends LinearLayout {
    //头部
    private View mHeaderTop;
    private View mHeaderBottom;
    //内容区域
    private View mContentView;
    private float screenWidth;
    private float screenHeight;
    private float mWidth;
    private float mTouchSlop;
    //头部header原始宽高
    private float mHeaderBottomMeasuredHeight;
    private float mHeaderBottomMeasuredWidth;
    private float mHeaderTopMeasuredHeight;
    private float mHeaderTopMeasuredWidth;

    private boolean isInit = true;
    //上层Header高度
    private float topHeaderHeight;
    //下层header高度
    private float bottomHeaderHeight;
    //上下层header原始高度差
    private float mDH;
    //更新状态后的header高度差
    private float mChangedDH;

    public ScrollerDemoView3(Context context) {
        this(context, null);
    }

    public ScrollerDemoView3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerDemoView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        mWidth = screenWidth * scalingFactor;
        //获取滑动手势默认值
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        //右下角为动画中心点
//        setPivotX(screenWidth);
//        setPivotY(screenHeight);
        post(new Runnable() {
            @Override
            public void run() {
                mHeaderTopMeasuredHeight = mHeaderTop.getMeasuredHeight();
                mHeaderBottomMeasuredHeight = mHeaderBottom.getMeasuredHeight();
                if (mDH == 0) {
                    mDH = mHeaderTopMeasuredHeight - mHeaderBottomMeasuredHeight;
                    mChangedDH = mDH;
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeaderTopMeasuredWidth = mWidth;
        mHeaderBottomMeasuredWidth = mWidth;
        Log.d(TAG, "onMeasure: mDH " + mDH);
        if (mDH != 0) {
            mHeaderTopMeasuredHeight = mHeaderBottomMeasuredHeight + mChangedDH;
            setHeaderH((int) mHeaderTopMeasuredHeight);
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mWidth, MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //设置头部宽高
    private void setHeaderH(int headerHtop) {
        //设置Header宽高
        ViewGroup.LayoutParams layoutParamsT = mHeaderTop.getLayoutParams();
        layoutParamsT.height = headerHtop;
        mHeaderTop.setLayoutParams(layoutParamsT);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderTop = findViewById(R.id.st_header);
        mHeaderBottom = findViewById(R.id.st_sticky);
        mContentView = findViewById(R.id.st_content);
    }


    private static final float MIN_SCALING_FACTOR = (float) 0.5;

    float mDownY;
    float mLastY;
    float mDiffY;
    float scalingFactor = MIN_SCALING_FACTOR;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float y = event.getRawY();
        float x = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mLastY = y;
//                mDownY = y;
//                Log.d(TAG, "onInterceptTouchEvent: ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                mDiffY = mLastY - y;
                mLastY = y;
                /*if (mDiffY > mTouchSlop)
                    return true;*/
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private static final String TAG = "ScrollerDemoView3";

    private float distance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getRawY();
        float x = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = y;
                mLastY = y;
                Log.d(TAG, "onTouchEvent: ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
//                mDiffY = mLastY - y;
                distance = y - mLastY;
                if (Math.abs(distance) < mTouchSlop) {
                    break;
                }
                mChangedDH = mChangedDH + distance;

                if (mChangedDH < 0) {
                    mChangedDH = 0;
                }
                if (mChangedDH > mDH) {
                    mChangedDH = mDH;
                }
                scalingFactor = 1 - (1 - MIN_SCALING_FACTOR) * mChangedDH / mDH;
                mWidth -= distance;
                requestLayout();
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(event);
    }

    private float calcuteWidth(float changedH) {
//        mHeaderTopMeasuredWidth
        return 0;
    }

    /**
     * @param diffH 两个头部的动态高度差
     */
    private float getScalFactor(float diffH) {
        float scalingFactor = (float) (1 - 0.2 * diffH / mDH);
        /*if (scalingFactor > MIN_SCALING_FACTOR - 0.1 && scalingFactor < MIN_SCALING_FACTOR) {
            scalingFactor = (float) 0.8;
        }*/

        /*if (scalingFactor > 0.95 && scalingFactor > 1) {
            scalingFactor = 1;
        }*/
        return scalingFactor;
    }
}