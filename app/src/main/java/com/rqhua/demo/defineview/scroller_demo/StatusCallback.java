package com.rqhua.demo.defineview.scroller_demo;

/**
 * Created by Administrator on 2018/6/19.
 */

public interface StatusCallback {
    //内容是否到达顶部
    boolean isContentTop(float touchSlop);
}