package com.rqhua.demo.defineview.demo2;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rqhua.demo.defineview.R;

/**
 * Created by Administrator on 2018/6/13.
 */

public class CustomeViewGroup extends RelativeLayout implements StatusCallback {
    //头部
    private View mHeader;
    private View mSticky;
    //内容区域
    private View mContentView;
    private View mMenuView;
    private float mWidth;
    private float screenHeight;
    private float mContentWidth;
    private float mMenuWidth;
    //默认宽度
    private float mContentDefaultWidth;
    private float mTouchSlop;
    //头部header原始宽高
    private float mHeaderBottomMeasuredHeight;
    private float mHeaderTopMeasuredHeight;

    //上下层header原始高度差
    private float mDH;
    //更新状态后的header高度差
    private float mChangedDH;
    //固定头的Alpha值
    private float mStickyAlpha;

    //======函数方程==========
    //1、计算头部高度差 mChangedH = -2*mDH * mContentWidth / mWidth + 2* mDH
    //-2*mDH / mWidth
    private float a1;
    //2* mDH
    private float b1;

    //2、计算固定头的Alpha值  mStickyAlpha = 2 * mWidth * mContentWidth + 1 - 2 * mWidth * mWidth
    //2 * mWidth
    private float a2;
    //1 - 2 * mWidth * mWidth
    private float b2;

    //======函数方程==========
    public CustomeViewGroup(Context context) {
        this(context, null);
    }

    public CustomeViewGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomeViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //根据不同的内容布局设置对应的内容是否到达顶部回调
        //默认回调为本身实现
//        setStatusCallback(this);
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        mWidth = displayMetrics.widthPixels;
//        screenHeight = displayMetrics.heightPixels;
        //默认宽度
//        mContentDefaultWidth = mWidth * MIN_SCALING_FACTOR;
//        mContentWidth = mContentDefaultWidth;
        //获取滑动手势默认值
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        post(new Runnable() {
            @Override
            public void run() {
                //初始化宽
                mWidth = getMeasuredWidth();
                mContentDefaultWidth = mWidth * MIN_SCALING_FACTOR;
                mContentWidth = mContentDefaultWidth;
                ViewGroup.LayoutParams layoutParams = mMenuView.getLayoutParams();
                mMenuWidth = mWidth * (1 - MIN_SCALING_FACTOR);
                layoutParams.width = (int) mMenuWidth;
                mMenuView.setLayoutParams(layoutParams);
                setChildWidth((int) mContentWidth);

                mHeaderTopMeasuredHeight = mHeader.getMeasuredHeight();
                mHeaderBottomMeasuredHeight = mSticky.getMeasuredHeight();
                //获取默认高度差
                mDH = mHeaderTopMeasuredHeight - mHeaderBottomMeasuredHeight;
                mChangedDH = mDH;

                //计算直线函数方程 a b 值
//                a1 = mDH / (mWidth * (MIN_SCALING_FACTOR - 1));
                a1 = mDH / -mMenuWidth;
//                    b1 = mDH / (1 - MIN_SCALING_FACTOR);
                b1 = -mWidth * a1;
//                a2 = 1 / (mWidth * (1 - MIN_SCALING_FACTOR));
                a2 = 1 / mMenuWidth;
                b2 = -MIN_SCALING_FACTOR * mWidth * a2;

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mDH != 0) {
            //动态改变头部高度及透明度
            mHeaderTopMeasuredHeight = mHeaderBottomMeasuredHeight + mChangedDH;
            setHeaderHight((int) mHeaderTopMeasuredHeight);
            setHeaderAlpha(mStickyAlpha);
            setChildWidth((int) mContentWidth);
        }
//        widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) mContentWidth, MeasureSpec.getMode(widthMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMenuView = findViewById(R.id.menu);
        mHeader = findViewById(R.id.st_header);
        mSticky = findViewById(R.id.st_sticky);
        mContentView = findViewById(R.id.st_content);
        Log.d(TAG, "onFinishInflate: ");
        setHeaderAlpha(mStickyAlpha);
    }

    private static final float MIN_SCALING_FACTOR = (float) 0.75;

    float mLastY;
    float mLastX;
    float mDiffY;
    float mDiffX;
    float mDownX;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getRawY();
        float x = ev.getRawX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                mLastX = x;
                mDownX = x;
                break;
            case MotionEvent.ACTION_MOVE:

//                mDiffY = y - mLastY;
                mDiffY = y - mLastY;
                mDiffX = x - mLastX;
                mLastY = y;
                mLastX = x;
                if (Math.abs(mDiffX) > Math.abs(mDiffY)) { //左右
                    //Log.d(TAG, "onInterceptTouchEvent: 左右");
                    if (mDiffX > 0) { //右滑
                        direction = 1;
                    } else { //左滑
                        direction = -1;
                    }
                } else { //上下
                    //Log.d(TAG, "onInterceptTouchEvent: 上下");
                    direction = 2;
                    //内容下滑事件传递给布局
                    if (mDiffY > 0 && mChangedDH == 0 && getStatusCallback().isContentTop(mTouchSlop) || mDiffY < 0 && mChangedDH > 0) {
                        if (mDownX < mMenuWidth && mContentWidth <= mContentDefaultWidth) {
                            return super.dispatchTouchEvent(ev);
                        }
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                direction = 0;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private int direction;
    int MIN_OFFSET_VALUE = 20;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                if (direction == 2) {
                    if (mDownX < mMenuWidth && mContentWidth <= mContentDefaultWidth) {
                        Log.d(TAG, "onInterceptTouchEvent: 上下默认");
                        return super.onInterceptTouchEvent(event);
                    }
                    Log.d(TAG, "onInterceptTouchEvent: 上下");
                    if (interceptUD()) return true;
                } else if (direction == 1 || direction == -1) {

                    Log.d(TAG, "onInterceptTouchEvent: 左右");
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    private boolean interceptUD() {
        boolean contentTop = getStatusCallback().isContentTop(mTouchSlop);
        //下滑
        Log.d(TAG, "interceptUD: contentTop " + contentTop);
        if (mDiffY > 0 && mChangedDH < mDH && contentTop) {
            Log.d(TAG, "interceptUD: 111111111111");
            return true;
        }
        //上滑
        if (mDiffY < 0 && mChangedDH > 0 && contentTop) {
            Log.d(TAG, "interceptUD: 222222222222");
            return true;
        }

        /*if (mChangedDH >= mTouchSlop) {
            return true;
        }*/
        return false;
    }

    private static final String TAG = "ScrollerDemoView3";


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE");
                if (direction == 2) {
                    updateStatus(mDiffY);
                    if (mChangedDH == 0 || mChangedDH == mDH) {
                        //content与布局滑动事件传递，通过cancel事件：上滑动传递
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                    }
                } else if (direction == 1) {
//                    mContentWidth = mContentDefaultWidth;
                    updateStatus(mDiffX);
                } else if (direction == -1) {
//                    mContentWidth = mWidth;
                    updateStatus(mDiffX);
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent: ACTION_CANCEL");
                if (direction == 2) {
                    updateStatus(mDiffY);
                } else if (direction == 1) {
//                    mContentWidth = mContentDefaultWidth;
                    updateStatus(mDiffX);
                } else if (direction == -1) {
//                    mContentWidth = mWidth;
                    updateStatus(mDiffX);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /*private void updatea() {
        Log.d(TAG, "updatea: mDiffX " + mDiffX);
        //改变透明度及宽度
        mContentWidth -= mDiffX;
        if (mContentWidth > mWidth)
            mContentWidth = mWidth;
        if (mContentWidth < mContentDefaultWidth)
            mContentWidth = mContentDefaultWidth;
        mChangedDH = a1 * mContentWidth + b1;
        mStickyAlpha = a2 * mContentWidth + b2;
        requestLayout();
    }*/

    private void updateStatus(float diff) {
        //改变透明度及宽度
        mContentWidth -= diff;
        if (mContentWidth > mWidth)
            mContentWidth = mWidth;
        if (mContentWidth < mContentDefaultWidth)
            mContentWidth = mContentDefaultWidth;
        mChangedDH = a1 * mContentWidth + b1;
        mStickyAlpha = a2 * mContentWidth + b2;
        requestLayout();
    }

    //设置头部高度
    private void setHeaderHight(int headerHight) {
        //设置Header高度
        ViewGroup.LayoutParams layoutParamsT = mHeader.getLayoutParams();
        layoutParamsT.height = headerHight;
        mHeader.setLayoutParams(layoutParamsT);
    }

    /**
     * @param stickyAlpha 固定头的透明度
     */
    //设置透明度
    private void setHeaderAlpha(float stickyAlpha) {
        mSticky.setAlpha(stickyAlpha);

        if (1 - stickyAlpha < 0.4)
            mHeader.setAlpha((float) 0.4);
        else
            mHeader.setAlpha(1 - stickyAlpha);
        if (stickyAlpha <= 0.2) {
            mSticky.setEnabled(false);
        } else {
            mSticky.setEnabled(true);
        }
    }

    //设置内容宽度，不包含Menu列表宽度
    private void setChildWidth(int childWidth) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getId() == R.id.menu) {
                continue;
            }
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            layoutParams.width = childWidth;
            childAt.setLayoutParams(layoutParams);
        }
    }

    private StatusCallback statusCallback;

    public StatusCallback getStatusCallback() {
        return statusCallback;
    }

    public void setStatusCallback(StatusCallback statusCallback) {
        this.statusCallback = statusCallback;
    }

    @Override
    public boolean isContentTop(float mTouchSlop) {
        Rect rect = new Rect();
        mContentView.getDrawingRect(rect);
        boolean b = rect.top <= mTouchSlop ? true : false;
        return b;
    }

}