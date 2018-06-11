package com.rqhua.demo.defineview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2018/6/5.
 */

public class DefineLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }


}
