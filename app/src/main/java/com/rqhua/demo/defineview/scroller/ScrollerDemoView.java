package com.rqhua.demo.defineview.scroller;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.rqhua.demo.defineview.R;


/**
 * Created by Administrator on 2018/6/11.
 */
public class ScrollerDemoView extends LinearLayout {
    private static final String TAG = "ScrollerDemoView";
    private Scroller scroller;
    private float mLastRawY;
    private float mY;
    private int mTouchSlop;
    //fling手势动作的最大速度值
    private int mMaximumVelocity;
    //fling手势动作的最小速度值
    private int mMinimumVelocity;
    //头
    private View mHeaderView;
    //粘连布局
    private View mStickyView;
    //内容
    private View mContentView;
    //头高度
    private int mHeaderHeight;

    private boolean mIsSticky;
    //滚动方向
    private Direction direction;

    public ScrollerDemoView(Context context) {
        this(context, null);
    }

    public ScrollerDemoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerDemoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context);
        // 获取TouchSlop值
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mMinimumVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
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
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mHeaderHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        acquireVelocityTracker(event);
        mY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //DOWN 事件停止 scroller
                if (!scroller.isFinished()) {
                    scroller.forceFinished(true);
                }
                mLastRawY = mY;
                break;
            case MotionEvent.ACTION_MOVE:
                diffY = mLastRawY - mY;
                mLastRawY = mY;
                //content与布局滑动事件传递，通过cancel事件：下滑动传递
                if (diffY < 0 && isContentTop()) {
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                //ACTION_UP时，执行fling
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) mVelocityTracker.getYVelocity();
                Log.d(TAG, "velocityY: " + velocityY);
                direction = velocityY < 0 ? Direction.UP : Direction.DOWN;
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
        mY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastRawY = mY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isSticky() && isContentTop()
                        || !isSticky() && !isContentTop()
                        || isSticky() && isContentTop() && scrolDirection(mLastRawY, mY) == Direction.DOWN) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                mLastRawY = 0;
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    float diffY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        mY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE ");
                scrollBy(0, (int) (diffY));
                if (isSticky()) {
                    //content与布局滑动事件传递，通过cancel事件：上滑动传递
                    event.setAction(MotionEvent.ACTION_DOWN);
                    dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                scrollBy(0, (int) (diffY));
                break;
//            case MotionEvent.ACTION_UP:
            //下滑回弹设置
////                scroller.startScroll(0, getScrollY(), 0, -getScrollY());
////                invalidate();
//
//                mLastRawY = 0;
//                break;
        }
        return super.onTouchEvent(event);
    }

    public void fling(int velocityY) {
        scroller.fling(0, getScrollY(), 0, velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        scroller.computeScrollOffset();
        mLastScrollerY = scroller.getCurrY();
        invalidate();
    }

    private int mLastScrollerY;

    @Override
    public void computeScroll() {
        //判断是否还在滚动，还在滚动为true
        if (scroller.computeScrollOffset()) {
            int currY = scroller.getCurrY();
            if (direction == Direction.UP) {
                if (isSticky()) {
                    int distance = scroller.getFinalY() - currY;
                    int duration = scroller.getDuration() - scroller.timePassed();
                    //内容滚动
                    flingContent(distance, duration);
                    scroller.forceFinished(true);
                } else {
                    //滚动未完成，布局继续滚动
                    scrollTo(scroller.getCurrX(), scroller.getCurrY());
                    postInvalidate();
                }
            } else {
                if (isContentTop()) {
                    int delta = currY - mLastScrollerY;
                    int toY = getScrollY() + delta;
                    scrollTo(0, toY);
                    if (getScrollY() == 0 && !scroller.isFinished()) {
                        scroller.forceFinished(true);
                    }
                }
            }
            mLastScrollerY = currY;
        }
        super.computeScroll();
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

    public boolean isSticky() {
        return mIsSticky;
    }

    private boolean isContentTop() {
        Rect rect = new Rect();
        mContentView.getDrawingRect(rect);
        return rect.top <= mTouchSlop ? true : false;
    }

    private void flingContent(int distance, int duration) {
        if (mContentView instanceof ScrollView) {
            ScrollView scrollView = ((ScrollView) mContentView);
            scrollView.fling(getScrollerVelocity(distance, duration));
        }
    }

    private int getScrollerVelocity(int distance, int duration) {
        if (scroller == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return (int) scroller.getCurrVelocity();
        } else {
            return distance / duration;
        }
    }

    private Direction scrolDirection(float lastY, float curY) {
        return lastY < curY ? Direction.DOWN : Direction.UP;
    }

    //跟踪触摸事件的速度
    private VelocityTracker mVelocityTracker;

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

    enum Direction {
        UP,
        DOWN
    }
}
