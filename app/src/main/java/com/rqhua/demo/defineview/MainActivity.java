package com.rqhua.demo.defineview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.demo1).setOnClickListener(this);
        findViewById(R.id.demo2).setOnClickListener(this);
    }

    private static final String TAG = "MainActivity";

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.demo1:
                startActivity(new Intent(this, ScrollerDemo1Activity.class));
                break;
            case R.id.demo2:
                startActivity(new Intent(this, ScrollerDemo2Activity.class));
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