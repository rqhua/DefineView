package com.rqhua.demo.defineview.article_writing_view;

/**
 * @author Create by rqhua
 * @date date 18-7-25
 * 文章内容布局尺寸大小变化回调，刷新背景View绘制回调
 */
public interface RefreshBgCallback {
    /**
     * @param value 上层View参数
     */
    void onRefresh(SizeValue value);
}
