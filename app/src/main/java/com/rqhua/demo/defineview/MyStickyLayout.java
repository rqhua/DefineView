package com.rqhua.demo.defineview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2018/5/30.
 */

public class MyStickyLayout extends LinearLayout {
    private static final String TAG = "MyStickyLayout";
    //头
    private View mHeaderView;
    //粘连布局
    private View mStickyView;
    //内容
    private View mContentView;
    //头高度
    private int mHeaderHeight;
    //头高度
    private int mStickyHeight;


    //能够进行手势滑动的距离
    private int mTouchSlop;

    //专门用于处理滚动效果的工具类
    private Scroller mScroller;
    //跟踪触摸事件的速度
    private VelocityTracker mVelocityTracker;
    //fling手势动作的最大速度值
    private int mMaximumVelocity;
    //fling手势动作的最小速度值
    private int mMinimumVelocity;

    //头部是否已经隐藏
    private boolean mIsSticky;


    private float mLastY;


    public MyStickyLayout(Context context) {
        this(context, null);
    }

    public MyStickyLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyStickyLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderView = findViewById(R.id.st_header);
        mStickyView = findViewById(R.id.st_sticky);
        mContentView = findViewById(R.id.st_content);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        mStickyHeight = mStickyView.getMeasuredHeight();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mHeaderHeight, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                Log.d(TAG, "dispatchTouchEvent: ACTION_DOWN");
                //停止Scroller 滚动：头部未完全隐藏、内容布局最上方显示
                if (!mScroller.isFinished() && !isSticky() && isContentTop()) {
                    mScroller.forceFinished(true);
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "dispatchTouchEvent: ACTION_MOVE");
                float dy = mLastY - y;
                //内容布局最上方显示，让Sticklout获取滑动事件
                if (Math.abs(dy) > mTouchSlop && isContentTop()) {
//                    mLastY = y;
//                    event.setAction(MotionEvent.ACTION_CANCEL);
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "dispatchTouchEvent: ACTION_UP");
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onInterceptTouchEvent: ACTION_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onInterceptTouchEvent: ACTION_MOVE");
                float dy = mLastY - y;
                //内容布局最上方显示，让布局获取滑动事件
                if (!isSticky() && Math.abs(dy) > mTouchSlop && isContentTop() || isSticky() && Math.abs(dy) > mTouchSlop && !isContentTop()) {
                    mLastY = y;
                    Log.d(TAG, "onInterceptTouchEvent: 拦截");
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onInterceptTouchEvent: ACTION_UP");
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: ACTION_DOWN");
                mLastY = y;
                /*if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }*/
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE");
                float dy = mLastY - y;
                Log.d(TAG, "onTouchEvent: dy = " + dy);
                if (Math.abs(dy) > mTouchSlop) {
                    //滚动布局
                    scrollBy(0, (int) (dy + 0.5));
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onTouchEvent: ACTION_CANCEL");
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: ACTION_UP");
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int currY = mScroller.getCurrY();
            scrollTo(0, currY);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > mHeaderHeight) {
            y = mHeaderHeight;
        }

        mIsSticky = y == mHeaderHeight;
        super.scrollTo(x, y);
    }

    private boolean isSticky() {
        return mIsSticky;
    }

    public void fling(int velocityY) {
        mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        mScroller.computeScrollOffset();
//        mLastScrollerY = mScroller.getCurrY();
        invalidate();
    }

    //将事件添加到速度跟踪器：所有的事件类型
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    //释放
    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public boolean isContentTop() {
        boolean isTop = false;
        int[] contentLocation = new int[2];
        getContentView().getLocationOnScreen(contentLocation);
        int[] stickyLocation = new int[2];
        getStickyView().getLocationOnScreen(stickyLocation);
        if (contentLocation[1] >= mStickyHeight + stickyLocation[1]) {
            isTop = true;
        }
        Log.d(TAG, "isContentTop: " + isTop);
        return isTop;
    }

    private View getContentView() {
        return mContentView;
    }

    private View getStickyView() {
        return mStickyView;
    }
}
