package com.example.hzxr.myapplication;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by hzxr on 2017/7/23.
 */

public class MyLayoutManager extends LinearLayoutManager {
    private boolean isScrollEnable = true;

    public  MyLayoutManager(Context context){
        super(context);
    }

    public void setScrollEnable(boolean scrollEnable) {
        isScrollEnable = scrollEnable;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnable&&super.canScrollVertically();
    }
}
