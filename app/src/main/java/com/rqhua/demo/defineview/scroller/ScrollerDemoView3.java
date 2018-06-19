package com.rqhua.demo.defineview.scroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
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
    //默认宽度
    private float mDefaultWidth;
    private float mTouchSlop;
    //头部header原始宽高
    private float mHeaderBottomMeasuredHeight;
    private float mHeaderTopMeasuredHeight;

    private boolean isInit = true;
    //上层Header高度
    private float topHeaderHeight;
    //下层header高度
    private float bottomHeaderHeight;
    //上下层header原始高度差
    private float mDH;
    //更新状态后的header高度差
    private float mChangedDH;
    //固定头的Alpha值
    private float mAlphaT;

    //======函数方程==========
    //1、计算头部高度差 mChangedH = -2*mDH * mWidth / screenWidth + 2* mDH
    //-2*mDH / screenWidth
    private float a1;
    //2* mDH
    private float b1;

    //2、计算固定头的Alpha值  mAlphaT = 2 * screenWidth * mWidth + 1 - 2 * screenWidth * screenWidth
    //2 * screenWidth
    private float a2;
    //1 - 2 * screenWidth * screenWidth
    private float b2;

    //======函数方程==========
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
        //默认宽度
        mDefaultWidth = screenWidth * MIN_SCALING_FACTOR;
        mWidth = mDefaultWidth;
        //获取滑动手势默认值
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        post(new Runnable() {
            @Override
            public void run() {
                mHeaderTopMeasuredHeight = mHeaderTop.getMeasuredHeight();
                mHeaderBottomMeasuredHeight = mHeaderBottom.getMeasuredHeight();
                if (mDH == 0) {
                    //获取默认高度差
                    mDH = mHeaderTopMeasuredHeight - mHeaderBottomMeasuredHeight;
                    mChangedDH = mDH;
                    //直线函数方程 a b 值
                    a1 = mDH / (screenWidth * (MIN_SCALING_FACTOR - 1));
//                    b1 = mDH / (1 - MIN_SCALING_FACTOR);
                    b1 = -screenWidth * a1;
                    a2 = 1 / (screenWidth * (1 - MIN_SCALING_FACTOR));
                    b2 = -MIN_SCALING_FACTOR * screenWidth * a2;
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDH != 0) {
            //动态改变头部高度及透明度
            mHeaderTopMeasuredHeight = mHeaderBottomMeasuredHeight + mChangedDH;
            setHeaderH((int) mHeaderTopMeasuredHeight);
            mHeaderBottom.setAlpha(mAlphaT);
            mHeaderTop.setAlpha(1 - mAlphaT);
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mWidth, MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //设置头部高度
    private void setHeaderH(int headerHtop) {
        //设置Header高度
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
        Log.d(TAG, "onFinishInflate: ");
        mHeaderBottom.setAlpha(mAlphaT);
        mHeaderTop.setAlpha(1 - mAlphaT);
    }

    private static final float MIN_SCALING_FACTOR = (float) 0.8;

    float mLastY;
    float mDiffY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                mDiffY = y - mLastY;
                distance = y - mLastY;
                mLastY = y;
                //内容下滑事件传递给布局
                if (mDiffY > 0 && mChangedDH == 0 && isContentTop()) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                //下滑
                if (mDiffY > 0 && mChangedDH < mDH /*&& isContentTop()*/) {
                    return true;
                }
                //上滑
                if (mDiffY < 0 && mChangedDH > 0 /*&& isContentTop()*/) {
                    return true;
                }

                if (mChangedDH >= mTouchSlop) {
                    return true;
                }

                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private static final String TAG = "ScrollerDemoView3";

    private float distance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                updateStatus();
                if (mChangedDH == 0 || mChangedDH == mDH) {
                    //content与布局滑动事件传递，通过cancel事件：上滑动传递
                    event.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent: ACTION_CANCEL");
                updateStatus();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateStatus() {
        //改变透明度及宽度
        mWidth -= distance;
        if (mWidth > screenWidth)
            mWidth = screenWidth;
        if (mWidth < mDefaultWidth)
            mWidth = mDefaultWidth;
        mChangedDH = a1 * mWidth + b1;
        mAlphaT = a2 * mWidth + b2;
        requestLayout();
    }

    private boolean isContentTop() {
        Rect rect = new Rect();
        mContentView.getDrawingRect(rect);
        boolean b = rect.top <= mTouchSlop ? true : false;
        return b;
    }
}