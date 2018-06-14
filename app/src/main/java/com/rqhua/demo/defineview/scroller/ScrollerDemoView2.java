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
import android.widget.LinearLayout;

import com.rqhua.demo.defineview.R;

/**
 * Created by Administrator on 2018/6/13.
 */

public class ScrollerDemoView2 extends LinearLayout {
    private View mHeaderTop;
    private View mHeaderBottom;
    private View mContentView;
    private int screenWidth;
    private int screenHeight;
    private int mTouchSlop;
    private int mHeaderBottomMeasuredHeight;
    private int mHeaderTopMeasuredHeight;
    private int mHeaderTopMeasuredWidth;

    private boolean isInit = true;
    //上层Header高度
    private float topHeaderHeight;
    //下层header高度
    private float bottomHeaderHeight;
    //上下层header原始高度差
    private float mDH;
    //更新状态后的header高度差
    private float mChangedDH;
    private float miniTopScal;

    public ScrollerDemoView2(Context context) {
        this(context, null);
    }

    public ScrollerDemoView2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerDemoView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DefineView);
        //headerView 高度
        topHeaderHeight = typedArray.getDimension(R.styleable.DefineView_topheader_height, 0);
        bottomHeaderHeight = typedArray.getDimension(R.styleable.DefineView_bottomheader_width, 0);
        typedArray.recycle();
        mHeaderTopMeasuredHeight = (int) topHeaderHeight;
        mHeaderBottomMeasuredHeight = (int) bottomHeaderHeight;
        mDH = topHeaderHeight - bottomHeaderHeight;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        //获取滑动手势默认值
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        //右下角为动画中心点
        setPivotX(screenWidth);
        setPivotY(screenHeight);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInit) {
            mHeaderTopMeasuredHeight = mHeaderTop.getMeasuredHeight();
            mHeaderBottomMeasuredHeight = mHeaderBottom.getMeasuredHeight();
        }

        mHeaderTopMeasuredWidth = mHeaderTop.getMeasuredWidth();

        Log.d(TAG, "onMeasure: mHeaderTopMeasuredHeight " + mHeaderTopMeasuredHeight);
        Log.d(TAG, "onMeasure: mHeaderBottomMeasuredHeight " + mHeaderBottomMeasuredHeight);
        //设置Header宽高
        ViewGroup.LayoutParams layoutParamsT = mHeaderTop.getLayoutParams();
        layoutParamsT.height = mHeaderTopMeasuredHeight;
        mHeaderTop.setLayoutParams(layoutParamsT);
        ViewGroup.LayoutParams layoutParamsB = mHeaderBottom.getLayoutParams();
        layoutParamsB.height = mHeaderBottomMeasuredHeight;
        mHeaderBottom.setLayoutParams(layoutParamsB);
        isInit = false;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setScaleX(scalingFactor);
        mHeaderTop = findViewById(R.id.st_header);
        mHeaderBottom = findViewById(R.id.st_sticky);
        mContentView = findViewById(R.id.st_content);
        mHeaderTop.setPivotX(mHeaderTopMeasuredWidth / 2);
        mHeaderTop.setPivotY(0);
        miniTopScal = (float) (1.0 * mHeaderBottomMeasuredHeight / mHeaderTopMeasuredHeight);
    }


    private static final float MIN_SCALING_FACTOR = (float) 0.8;

    float mDownY;

    float mLastY;
    float mDiffY;
    float scalingFactor = 1;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float y = event.getRawY();
        float x = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                Log.d(TAG, "onInterceptTouchEvent: ACTION_DOWN");
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

    private static final String TAG = "ScrollerDemoView2";

    private float distance;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getRawY();
        float x = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = y;
                mLastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                mDiffY = mLastY - y;
                if (Math.abs(mDiffY) < mTouchSlop) {
                    break;
                }
                /*if (mDiffY > mTouchSlop)
                    return true;*/
                distance = y - mLastY;
                mChangedDH = mChangedDH + distance;


                if (mChangedDH < 0) {
                    mChangedDH = 0;
                } else {
                    scalingFactor = (float) (1 - 0.2 * mChangedDH / mDH);
                    if (scalingFactor > 0.7 && scalingFactor < 0.8) {
                        scalingFactor = (float) 0.8;
                    }
                    float alphaTop = -5 * scalingFactor + 5;
                    float scaleTop = (-mDH) * 5 * scalingFactor / mHeaderTopMeasuredHeight - (float) (4.0 * miniTopScal) + 5;
                    if ((scaleTop - miniTopScal) < 0.1) {
                        scaleTop = miniTopScal;
                    }
                    float alphaBottom = 5 * scalingFactor - 4;

                    mHeaderTop.setAlpha(alphaTop);
                    mHeaderBottom.setAlpha(alphaBottom);
                    mHeaderTop.setScaleY(scaleTop);

                    setScaleX(scalingFactor);
//                    setScaleY(scalingFactor);
                }
                if (mChangedDH > mDH) {
                    mChangedDH = mDH;
                } else {
                    scalingFactor = (float) (1 - 0.2 * mChangedDH / mDH);
                    setScaleX(scalingFactor);
//                    setScaleY(scalingFactor);
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return super.onTouchEvent(event);
    }

}
