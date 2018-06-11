package com.rqhua.demo.defineview.scroller;

/**
 * Created by Administrator on 2018/6/11.
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * 仿微信刷新
 * @author Nipuream
 */
public class WXLayout extends LinearLayout {

    private static final String TAG = "WXLayout";
    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private float mLastMotionY;
    private float  mInitialMotionY;
    private float resistance = 0.6f;
    private Scroller mScroller;
    private ListView mListView;
    private boolean isMove = false;
    private int duration = 300;
    private ScrollRershListener l;
    private boolean isRersh = false;

    public WXLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(final Context context){
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledTouchSlop();
        DecelerateInterpolator interpolator = new DecelerateInterpolator();
        mScroller = new Scroller(context,interpolator);
        post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mListView = (ListView) WXLayout.this.getChildAt(0);
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        final  int action = ev.getAction();

        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
            mIsBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
            return true;
        }

        switch(action){
            case MotionEvent.ACTION_DOWN:{
                mLastMotionY = mInitialMotionY = ev.getY();
                mIsBeingDragged = false;
                break;
            }
            case MotionEvent.ACTION_MOVE:{
                final float y = ev.getY(), x = ev.getX();
                final float diff, absDiff;
                diff = y - mLastMotionY;
                absDiff = Math.abs(diff);
                if(absDiff > mTouchSlop){
                    if(diff > 1){
                        if(mListView.getFirstVisiblePosition()==0){
                            View view = mListView.getChildAt(0);
                            Rect rect = new Rect();
                            view.getLocalVisibleRect(rect);
                            if(rect.top == 0){
                                mLastMotionY = y;
                                mIsBeingDragged = true;
                            }
                        }
                    }
                }
                break;
            }
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        //如果碰触到控件的边缘，就不接受这一系列的action了
        if (event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() != 0) {
            return false;
        }

        //如果Scroller正在滑动，就不接受这次事件了
        if(isMove){
            return false;
        }

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                mLastMotionY = mInitialMotionY = event.getY();
                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                if (mIsBeingDragged) {
                    if(l!=null && !isRersh){
                        l.startRersh();
                        isRersh = true;
                    }
                    mLastMotionY = event.getY();
                    float moveY = mLastMotionY - mInitialMotionY;
                    pullEvent(moveY);
                    return true;
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:{
                if(mIsBeingDragged){
                    mIsBeingDragged = false;
                    startMoveAnim(getScrollY(), Math.abs(getScrollY()), duration);
                    if(l!= null && isRersh && (event.getY() - mInitialMotionY) > 0){
                        l.endRersh(event.getY() - mInitialMotionY);
                        isRersh = false;
                    }
                    return true;
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    private void pullEvent(float moveY){
        if(l != null){
            l.Rersh(moveY);
        }
        if(moveY > 0){
            int value = (int) Math.abs(moveY);
            scrollTo(0, - (int)(value*resistance));
        }
    }

    public void startMoveAnim(int startY, int dy, int duration) {
        isMove = true;
        mScroller.startScroll(0, startY, 0, dy, duration);
        invalidate();//通知UI线程的更新
    }

    @Override
    public void computeScroll() {
        //判断是否还在滚动，还在滚动为true
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //更新界面
            postInvalidate();
            isMove = true;
        } else {
            isMove = false;
        }
        super.computeScroll();
    }

    public interface ScrollRershListener{
        void Rersh(float value);
        void startRersh();
        void endRersh(float value);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setOnScrollRershListener(ScrollRershListener l){
        this.l = l;
    }

}