package com.rqhua.demo.defineview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SnapHelperActivity extends AppCompatActivity implements MyLayoutManager.OnViewPagerListener {
    private static final String TAG = "SnapHelperActivity";
    private RecyclerView recyclerView;
    private AbsAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_helper);
        recyclerView = ((RecyclerView) findViewById(R.id.recyclerview));
//        MyLayoutManager layoutManager = new MyLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        EchelonLayoutManager layoutManager = new EchelonLayoutManager(this);
//        layoutManager.setOnViewPagerListener(this);
//        recyclerView.setLayoutManager(layoutManager);
        adapter = new AbsAdapter<String>(this) {
            @Override
            protected void onBindDataToView(CommonHolder holder, String bean, int position) {
                holder.setText(R.id.text, bean + " " + position);
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_list_layout;
            }
        };
        recyclerView.setAdapter(adapter);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("item " + i);
        }
        adapter.addAll(data, true);
    }

    @Override
    public void onPageRelease(boolean isNext, int position) {
        Log.d(TAG, "onPageRelease: isNext " + isNext + " position " + position);
    }

    @Override
    public void onPageSelected(int position, boolean isBottom) {
        Log.d(TAG, "onPageSelected: isBottom " + isBottom + " position " + position);
    }

    @Override
    public void onLayoutComplete() {
        Log.d(TAG, "onLayoutComplete: isBottom ");
    }
}