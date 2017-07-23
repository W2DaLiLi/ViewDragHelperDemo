package com.example.hzxr.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        RecyclerView rv = (RecyclerView) findViewById(R.id.menu_list);
        MyLayoutManager  layoutManager = new MyLayoutManager(this);
        layoutManager.setOrientation(MyLayoutManager.HORIZONTAL);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(new MyAdapter());
    }
}
