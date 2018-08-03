package com.rqhua.demo.defineview.article_writing_view;

import android.graphics.Rect;

/**
 * @author Create by rqhua
 * @date date 18-7-25
 */
public class SizeValue {
    //draw 区域
    Rect rect;
    int paddingTop;
    int paddingBottom;
    int paddingRight;
    int paddingLeft;
    //行高
    int lineHight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SizeValue value = ((SizeValue) o);
        return rect.equals(value.rect) && lineHight == value.lineHight
                && paddingTop == value.paddingTop && paddingBottom == value.paddingBottom
                && paddingRight == value.paddingRight && paddingLeft == value.paddingLeft;
    }

    @Override
    protected SizeValue clone() {
        SizeValue sizeValue = new SizeValue();
        sizeValue.rect = rect;
        sizeValue.lineHight = lineHight;
        sizeValue.paddingBottom = paddingBottom;
        sizeValue.paddingTop = paddingTop;
        sizeValue.paddingLeft = paddingLeft;
        sizeValue.paddingRight = paddingRight;
        return sizeValue;
    }

    @Override
    public String toString() {
        return "SizeValue \nrect=" + rect +
                "\npaddingTop=" + paddingTop +
                "\npaddingBottom=" + paddingBottom +
                "\npaddingRight=" + paddingRight +
                "\npaddingLeft=" + paddingLeft +
                "\nlineHight=" + lineHight;
    }
}
