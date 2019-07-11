package com.PrivateRouter.PrivateMail.view.common;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

public class SpeedyLinearLayoutManager extends LinearLayoutManager {


    private static final float MILLISECONDS_PER_INCH = 150f; //default is 25f (bigger = slower)
    RecyclerView recyclerView;
    public SpeedyLinearLayoutManager(RecyclerView recyclerView, Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setSmoothScrollbarEnabled(false);
        this.recyclerView = recyclerView;
    }


/*
    @Override
    public int scrollVerticallyBy(int delta, RecyclerView.Recycler recycler, RecyclerView.State state)
    {
        int value = (int) Math.min(super.scrollVerticallyBy((int) (delta ), recycler, state)*0.7f, 1);
        Log.d("123", "value="+value);
        return value;
    }*/
}