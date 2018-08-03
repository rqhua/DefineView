package com.rqhua.demo.defineview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.rqhua.demo.defineview.article_writing_view.ArticleWritingActivity;
import com.rqhua.demo.defineview.scroller_demo.ScrollerDemoActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.scrollerdemo).setOnClickListener(this);
        findViewById(R.id.article).setOnClickListener(this);
    }

    private static final String TAG = "MainActivity";

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.scrollerdemo:
                startActivity(new Intent(this, ScrollerDemoActivity.class));
                break;
            case R.id.article:
                startActivity(new Intent(this, ArticleWritingActivity.class));
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}