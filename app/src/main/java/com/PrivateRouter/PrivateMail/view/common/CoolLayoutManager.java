package com.PrivateRouter.PrivateMail.view.common;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class CoolLayoutManager extends LinearLayoutManager {
    private static final int SMOOTH_VALUE = 100;

    public CoolLayoutManager(Context context) {
        super(context);
        setSmoothScrollbarEnabled(false);
    }

    //Computes the vertical size of the scrollbar indicator (thumb)
    @Override
    public int computeVerticalScrollExtent(RecyclerView.State state) {
        final int count = getChildCount();
        if (count > 0) {
            return SMOOTH_VALUE * 3;
        }
        return 0;
    }

    //Computes the vertical size of all the content (scrollbar track)
    @Override
    public int computeVerticalScrollRange(RecyclerView.State state) {
        return Math.max((getItemCount() - 1) * SMOOTH_VALUE, 0);
    }

    //Computes the vertical distance from the top of the screen (scrollbar position)
    @Override
    public int computeVerticalScrollOffset(RecyclerView.State state) {
        final int count = getChildCount();

        if (count <= 0) {
            return 0;
        }
        if (findLastCompletelyVisibleItemPosition() == getItemCount() - 1) {
            return Math.max((getItemCount() - 1) * SMOOTH_VALUE, 0);
        }

        int heightOfScreen;
        int firstPos = findFirstVisibleItemPosition();
        if (firstPos == RecyclerView.NO_POSITION) {
            return 0;
        }
        View view = findViewByPosition(firstPos);
        if (view == null) {
            return 0;
        }
        // Top of the view in pixels
        final int top = getDecoratedTop(view);
        int height = getDecoratedMeasuredHeight(view);
        if (height <= 0) {
            heightOfScreen = 0;
        } else {
            heightOfScreen = Math.abs(SMOOTH_VALUE * top / height);
        }
        if (heightOfScreen == 0 && firstPos > 0) {
            return SMOOTH_VALUE * firstPos - 1;
        }
        return (SMOOTH_VALUE * firstPos) + heightOfScreen;
    }
}