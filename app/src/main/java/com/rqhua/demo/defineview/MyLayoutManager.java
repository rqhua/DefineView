package com.rqhua.demo.defineview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Administrator on 2018/6/5.
 */

public class MyLayoutManager extends LinearLayoutManager {

    private static final String TAG = "MyLayoutManager";

    private PagerSnapHelper mPagerSnapHelper;
//    private LinearSnapHelper mPagerSnapHelper;
    private RecyclerView mRecyclerView;


    public MyLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
    }


    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mPagerSnapHelper.attachToRecyclerView(view);
        mRecyclerView = view;
        mRecyclerView.addOnChildAttachStateChangeListener(listener);
    }

    @Override
    public void onScrollStateChanged(int state) {
        switch (state) {
            case RecyclerView.SCROLL_STATE_IDLE:
                View viewIdle = mPagerSnapHelper.findSnapView(this);
                int positionIdle = getPosition(viewIdle);
                if (mOnViewPagerListener != null && getChildCount() == 1) {
                    mOnViewPagerListener.onPageSelected(positionIdle, positionIdle == getItemCount() - 1);
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                View viewDrag = mPagerSnapHelper.findSnapView(this);
                int positionDrag = getPosition(viewDrag);
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                View viewSettling = mPagerSnapHelper.findSnapView(this);
                int positionSettling = getPosition(viewSettling);
                break;
        }
    }

    private int mDrift;

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.mDrift = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    private RecyclerView.OnChildAttachStateChangeListener listener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {

        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            Log.d(TAG, "onViewDetachedFromWindow: mDrift = " + mDrift);
            if (mDrift >= 0) {
                if (mOnViewPagerListener != null)
                    mOnViewPagerListener.onPageRelease(true, getPosition(view));
            } else {
                if (mOnViewPagerListener != null)
                    mOnViewPagerListener.onPageRelease(false, getPosition(view));
            }
        }
    };

    public void setOnViewPagerListener(OnViewPagerListener mOnViewPagerListener) {
        this.mOnViewPagerListener = mOnViewPagerListener;
    }

    private OnViewPagerListener mOnViewPagerListener;

    public interface OnViewPagerListener {
        /*释放的监听*/
        void onPageRelease(boolean isNext, int position);

        /*选中的监听以及判断是否滑动到底部*/
        void onPageSelected(int position, boolean isBottom);

        /*布局完成的监听*/
        void onLayoutComplete();
    }
}
