package com.PrivateRouter.PrivateMail.view.utils;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomLinearLayoutManager  extends LinearLayoutManager {
    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}