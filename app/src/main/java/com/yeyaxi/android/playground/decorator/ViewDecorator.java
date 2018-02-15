package com.yeyaxi.android.playground.decorator;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ViewDecorator extends RecyclerView.ItemDecoration {
    private int hPadding;
    private int vPadding;

    public ViewDecorator(int paddingHorizontal, int paddingVertical) {
        this.hPadding = paddingHorizontal;
        this.vPadding = paddingVertical;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 0) {
            outRect.left = hPadding;
            outRect.right = 0;
        } else if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.left = hPadding;
            outRect.right = hPadding;
        } else {
            outRect.left = hPadding;
            outRect.right = 0;
        }
        outRect.top = vPadding;
        outRect.bottom = vPadding;
    }
}
