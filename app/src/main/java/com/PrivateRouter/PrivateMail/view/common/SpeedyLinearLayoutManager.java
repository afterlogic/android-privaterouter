package com.PrivateRouter.PrivateMail.view.common;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        Logger.d("123", "value="+value);
        return value;
    }*/
}