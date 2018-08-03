package com.rqhua.demo.defineview.article_writing_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @author Create by rqhua
 * @date date 18-7-25
 * 1、此EditText最少行数是3：
 * 2、第一行和最后一行空出来不显示内容
 * 3、可编辑的行为第二行 至 倒数第二行
 * 因为5.0以后，EditText第一行和最后一行不添加行间距
 * 导致在第一行或者最后一行末尾换行时，新起的行也没有行间距，排版出现问题。
 */
public class ArticleEditText extends AppCompatEditText {
    private static final String TAG = "ArticleEditText";

    //换行字符 char
    private static final char LINE_BREAK_CHAR = '\n';
    //换行字符 String
    private static final String LINE_BREAK = "\n";

    //无内容: getText().toString() -> "\n"
    private static final String NO_CONTENT_LINE = LINE_BREAK;

    //默认显示内容，当内容为空或者无内容时设置显示
    private static final String BLANK_CONTENT = LINE_BREAK + LINE_BREAK;

    private RefreshBgCallback callback;
    private SizeValue sizeValue;
    private Paint mPaint;
    private int lineStartX;
    private int lineStopX;
    private int paddingTop;

    public ArticleEditText(Context context) {
        super(context);
        init();
    }

    public ArticleEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArticleEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    private static String text;

    private void init() {
        sizeValue = new SizeValue();
        sizeValue.rect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.LTGRAY);
        mPaint.setPathEffect(new DashPathEffect(new float[]{20, 10}, 0));
//        setLineSpacing(10, 1.2f);
        textWatcher();
        post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: post");
                //初始状态的光标位置
                selectionOption();

                text = getText().toString();
                if (TextUtils.isEmpty(text)) {
                    text = BLANK_CONTENT;
                }

                if (!startWithLineBreak(text)) {
                    text = LINE_BREAK + text;
                }

                if (!endWithLineBreak(text)) {
                    text = text + LINE_BREAK;
                }

                setText(text);
            }
        });
    }

    private void textWatcher() {
        addTextChangedListener(new TextWatcher() {
            private int selectionStart;
            private String textPre;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s != null) {
                    textPre = s.toString();
                }
                selectionStart = getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


            @Override
            public void afterTextChanged(Editable editable) {

                //1:显示默认三行
                //1.1 空：显示默认三行
                if (editable == null) {
                    text = BLANK_CONTENT;
                    setText(text);
                    return;
                }
                //1.2 无内容时：显示默认三行
                text = editable.toString();
                if (TextUtils.isEmpty(text) || NO_CONTENT_LINE.equals(text)) {
                    text = BLANK_CONTENT;
                    setText(text);
                    return;
                }
                //1.3 空和无内容时，光标定位到第二行的开始
                /*if (setDefaultSelection) {
                    setSelection(1);
                    setDefaultSelection = false;
                }
*/
                //修改内容之后，第一个换行符的位置
                if (!startWithLineBreak(text)) {
                    text = LINE_BREAK + text;
                    if (!endWithLineBreak(text)) {
                        text = text + LINE_BREAK;
                    }
                    setText(text);
                }
                /*lineBreakIndexCd = text.indexOf(LINE_BREAK);
                if (lineBreakIndexCd != 0) { //修改之后，开头不是换行符
                    *//*if (lineBreakIndexBd == 0) {
                        text = LINE_BREAK + text;
                        setText(text);
                        return;
                    }*//*
                    text = LINE_BREAK + text;

                }*/
                if (!endWithLineBreak(text)) {
                    text = text + LINE_BREAK;
                    setText(text);
                }
                if (!text.equals(textPre)) {
                    setText(text);
                }

                if (textPre != null) {
                    if (text.length() > textPre.length()) {
                        //添加文字
                        setSelection(selectionStart + 1);
                    } else if (text.length() < textPre.length()) {
                        //删除了文字
                        setSelection(selectionStart - 1);
                    } else {
                        setSelection(selectionStart);
                    }

                }
            }
        });
    }


    /**
     * 处理光标位置，不能在第一行和最后一行
     * 处于第一行时，定位到第二行的开始位置
     * 处于最后一行时，定位到倒数第二行的结束位置
     */
    private void selectionChanged(int selStart, int selEnd) {
        String currentText = getText().toString();
        int selectionStart = getSelectionStart();
        if (selectionStart == 0 && !TextUtils.isEmpty(currentText) && currentText.length() > 1) {
            //光标在文字的开头，定位到开头换行符后
            setSelection(1);
        } else if (!TextUtils.isEmpty(currentText) && currentText.length() > 1 && selectionStart == currentText.length()) {
            //光标在文字最后，定位到结束换行符后
            setSelection(currentText.length() - 1);
        }
    }

    /**
     * 处理光标位置，不能在第一行和最后一行
     * 处于第一行时，定位到第二行的开始位置
     * 处于最后一行时，定位到倒数第二行的结束位置
     */
    private void selectionOption() {
        String currentText = getText().toString();
        int selectionStart = getSelectionStart();
        if (selectionStart == 0 && !TextUtils.isEmpty(currentText) && currentText.length() > 1) {
            //光标在文字的开头，定位到开头换行符后
            setSelection(1);
        } else if (!TextUtils.isEmpty(currentText) && currentText.length() > 1 && selectionStart == currentText.length()) {
            //光标在文字最后，定位到结束换行符后
            setSelection(currentText.length() - 1);
        }
    }


    public RefreshBgCallback getCallback() {
        return callback;
    }

    /**
     * 设置尺寸变化回调
     *
     * @param callback
     */
    public void setCallback(RefreshBgCallback callback) {
        this.callback = callback;
    }

    //换行符号开头
    private boolean startWithLineBreak(String text) {
        if (TextUtils.isEmpty(text) || text.length() < 1)
            return false;
        if (text.charAt(0) != LINE_BREAK_CHAR) {
            return false;
        }
        return true;
    }

    //换行符结尾
    private boolean endWithLineBreak(String text) {
        if (TextUtils.isEmpty(text) || text.length() < 1)
            return false;
        if (text.lastIndexOf(LINE_BREAK) != text.length() - 1) {
            return false;
        }
        return true;
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        selectionOption();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        lineStartX = getPaddingLeft();
        lineStopX = getMeasuredWidth() - getPaddingRight();
        paddingTop = getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawline(canvas);
    }


    //画线
    private void drawline(Canvas canvas) {
        long lineRange = getLineRangeForDraw(canvas);
        //firstLine = 0
        int firstLine = TextUtil.unpackRangeStartFromLong(lineRange);
        //lastLine = lineRange
        int lastLine = TextUtil.unpackRangeEndFromLong(lineRange);
        int offset = 0;
        int lastOffset = 0;
        for (int i = firstLine; i <= lastLine; i++) {
            //文字基准线： 下一行的顶部 - 本行的descent
            //下一行的顶部
            int lbottom = getLayout().getLineTop(i + 1);
            //基准线：在文字下划线位置
            int lbaseline = lbottom - getLayout().getLineDescent(i);
            //加上基准线到下一行顶部距离的一半，作为画线位置
            //如果设置有padding值，还需要加上paddingTop的偏移量
            offset = (lbottom - lbaseline) / 2 + paddingTop;
            lbaseline += offset;
            //倒数第二行，单独处理：添加上一行的偏移量
            if (i == lastLine - 1) {
                lbaseline += lastOffset;
            }
            lineStartX = getPaddingLeft();
            lineStopX = getMeasuredWidth() - getPaddingRight();
            //首行和最后一行不可操作，不划线
            if (i != firstLine && i != lastLine)
                canvas.drawLine(lineStartX, lbaseline, lineStopX, lbaseline, mPaint);
            lastOffset = offset - paddingTop;
        }
    }

    //行数
    public long getLineRangeForDraw(Canvas canvas) {
        int dtop, dbottom;
        Rect rect = new Rect();

        if (!canvas.getClipBounds(rect)) {
            // Negative range end used as a special flag
            return TextUtil.packRangeInLong(0, -1);
        }

        dtop = rect.top;
        dbottom = rect.bottom;

        final int top = Math.max(dtop, 0);
        final int bottom = Math.min(getLayout().getLineTop(getLayout().getLineCount()), dbottom);

        if (top >= bottom)
            return TextUtil.packRangeInLong(0, -1);
        return TextUtil.packRangeInLong(getLayout().getLineForVertical(top), getLayout().getLineForVertical(bottom));
    }

    /**
     * Return the text offset after the last visible character (so whitespace
     * is not counted) on the specified line.
     */
    private int getLineVisibleEnd(int line, int start, int end) {
        CharSequence text = getText();
        char ch;
        if (line == getLineCount() - 1) {
            return end;
        }

        for (; end > start; end--) {
            ch = text.charAt(end - 1);

            if (ch == LINE_BREAK_CHAR) {
                return end - 1;
            }

            if (ch != ' ' && ch != '\t') {
                break;
            }

        }

        return end;
    }

    private static class TextUtil {
        public static int unpackRangeStartFromLong(long range) {
            return (int) (range >>> 32);
        }

        public static int unpackRangeEndFromLong(long range) {
            return (int) (range & 0x00000000FFFFFFFFL);
        }

        public static long packRangeInLong(int start, int end) {
            return (((long) start) << 32) | end;
        }
    }

}