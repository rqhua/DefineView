package com.rqhua.demo.defineview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class ScrollerDemo2Activity extends Activity implements View.OnClickListener {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.main2);
        findViewById(R.id.st_header).setOnClickListener(this);
        findViewById(R.id.st_sticky).setOnClickListener(this);
        findViewById(R.id.st_content).setOnClickListener(this);
        recyclerView = ((RecyclerView) findViewById(R.id.recyclerview));
    }

    private static final String TAG = "MainActivity";

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.st_header:
                Log.d(TAG, "onClick st_header");
                Toast.makeText(ScrollerDemo2Activity.this, "Click st_header", Toast.LENGTH_SHORT).show();
                break;
            case R.id.st_sticky:
                Log.d(TAG, "onClick st_sticky");
                Toast.makeText(ScrollerDemo2Activity.this, "Click st_sticky", Toast.LENGTH_SHORT).show();
                break;
            case R.id.st_content:
                Log.d(TAG, "onClick st_content");
                Toast.makeText(ScrollerDemo2Activity.this, "Click st_content", Toast.LENGTH_SHORT).show();
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