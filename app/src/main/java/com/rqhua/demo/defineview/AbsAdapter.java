package com.rqhua.demo.defineview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsAdapter<T> extends RecyclerView.Adapter<AbsAdapter.CommonHolder> {

    protected List<T> mBeans;
    protected Context mContext;
    protected boolean mAnimateItems = false;
    protected int mLastAnimatedPosition = -1;

    private int headerCount = 0;
    private int footerCount = 0;


    public AbsAdapter(Context context) {
        if (context == null)
            throw new NullPointerException("Context Can not be Null");
        mContext = context;

        mBeans = new ArrayList<>();
    }

    @Override
    public CommonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(getItemLayoutID(viewType), parent, false);
        CommonHolder holder = new CommonHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CommonHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        if (position > getHeaderCount() - 1)
            onBindDataToView(holder, mBeans.get(position - getHeaderCount()), position);
        else
            onBindDataToView(holder, mBeans.get(position), position);
    }

    /**
     * 绑定数据到Item的控件中去
     *
     * @param holder
     * @param bean
     */
    protected abstract void onBindDataToView(CommonHolder holder, T bean, int position);

    /**
     * 取得ItemView的布局文件
     *
     * @return
     */
    public abstract int getItemLayoutID(int viewType);

    public int getHeaderCount() {
        return headerCount;
    }

    /**
     * 设置header和footer之后，设置对应的个数，以保证item序列显示正常
     *
     * @param headerCount
     */
    public void setHeaderCount(int headerCount) {
        this.headerCount = headerCount;
    }

    public int getFooterCount() {
        return footerCount;
    }

    /**
     * 设置header和footer之后，设置对应的个数，以保证item序列显示正常
     *
     * @param footerCount
     */
    public void setFooterCount(int footerCount) {
        this.footerCount = footerCount;
    }

    @Override
    public int getItemCount() {
        return mBeans == null ? 0 + getHeaderCount() + getFooterCount() : mBeans.size() + getHeaderCount() + getFooterCount();
    }

    public List<T> getBeans() {
        if (mBeans == null) {
            mBeans = new ArrayList<>();
        }
        return mBeans;
    }

    public void add(T bean) {
        if (mBeans == null)
            mBeans = new ArrayList<>();
        if (bean == null) {
            return;
        }
        mBeans.add(bean);
        dataChanged();
        notifyDataSetChanged();
    }

    public void addAll(List<T> beans) {
        addAll(beans, false);
    }

    public void addAll(List<T> beans, boolean clearPrevious) {
        if (mBeans == null)
            mBeans = new ArrayList<>();
        if (clearPrevious) mBeans.clear();
        if (beans == null) {
            return;
        }
        mBeans.addAll(beans);
        dataChanged();
        notifyDataSetChanged();
    }

    public void clear() {
        if (mBeans == null)
            return;
        mBeans.clear();
        dataChanged();
        notifyDataSetChanged();
    }

    public void remove(T bean) {
        if (mBeans == null)
            return;
        if (bean == null) {
            return;
        }

        mBeans.remove(bean);
        dataChanged();
        notifyDataSetChanged();
    }

    public void remove(List<T> beans) {
        if (mBeans == null)
            return;
        if (beans == null) {
            return;
        }

        mBeans.removeAll(beans);
        dataChanged();
        notifyDataSetChanged();
    }

    /**
     * {@link #notifyDataSetChanged()} 数据源改变之调用
     */
    protected void dataChanged() {

    }

    /***
     * item的加载动画
     *
     * @param view
     * @param position
     */
    private void runEnterAnimation(final View view, int position) {
        if (!mAnimateItems) {
            return;
        }
        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            view.setAlpha(0);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Animation animation = AnimationUtils.loadAnimation(view.getContext(),
                            R.anim.slide_in_right);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            view.setAlpha(1);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    view.startAnimation(animation);
                }
            }, 100);
        }
    }

    public static class CommonHolder extends RecyclerView.ViewHolder {
        private final SparseArray<View> mViews;
        public View itemView;

        public CommonHolder(View itemView) {
            super(itemView);
            this.mViews = new SparseArray<>();
            this.itemView = itemView;
            //添加Item的点击事件
        }


        public <T extends View> T getView(int viewId) {

            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public void setText(int viewId, String text) {
            TextView tv = getView(viewId);
            tv.setText(text);
        }

        /**
         * 加载drawable中的图片
         *
         * @param viewId
         * @param resId
         */
        public void setImage(int viewId, int resId) {
            ImageView iv = getView(viewId);
            iv.setImageResource(resId);
        }

        public void setBitmap(int viewId, Bitmap bitmap) {
            if (bitmap == null)
                return;
            ImageView iv = getView(viewId);
            iv.setImageBitmap(bitmap);
        }

        public void setVisible(int viewId, int visibility) {
            getView(viewId).setVisibility(visibility);
        }

        /**
         * 加载网络上的图片
         *
         * @param viewId
         * @param url
         */
        public void setImageFromInternet(int viewId, String url) {
            ImageView iv = getView(viewId);
//            ImageRequest imageRequest = new ImageRequest.Builder().imgView(iv).url(url).create();
//            ImageLoader.getProvider().loadImage(imageRequest);
        }

        public void setOnClickListener(int viewId, View.OnClickListener onClickListener) {
            getView(viewId).setOnClickListener(onClickListener);
        }

        public void setOnItemClickListener(View.OnClickListener onItemClickListener) {
            this.itemView.setOnClickListener(onItemClickListener);
        }
    }
}