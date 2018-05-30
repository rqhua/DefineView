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
        mContentView = findViewById(R.id.st_content);
        mHeaderView = findViewById(R.id.st_header);
        mStickyView = findViewById(R.id.st_sticky);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mHeaderHeight = mHeaderView.getMeasuredHeight();
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mHeaderHeight, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - event.getY();
                Log.d(TAG, "onTouchEvent: dy = " + dy);
                if (Math.abs(dy) > mTouchSlop) {
                    //滚动布局
                    scrollBy(0, (int) (dy + 0.5));
                }
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    fling(-velocityY);
                }
                recycleVelocityTracker();
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
}
