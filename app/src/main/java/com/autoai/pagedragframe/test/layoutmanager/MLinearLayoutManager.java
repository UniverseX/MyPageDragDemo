package com.autoai.pagedragframe.test.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MLinearLayoutManager extends RecyclerView.LayoutManager {

    private int mTotalHeight;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int itemCount = getItemCount();
        Log.d("zxl_test","MLinearLayoutManager -- onLayoutChildren: itemCount = " + itemCount);

        int offsetY = 0;
        for (int i = 0; i < getItemCount(); i++) {
            View scrap = recycler.getViewForPosition(i);
            addView(scrap);

            measureChildWithMargins(scrap, 0, 0);

            int perItemWidth = getDecoratedMeasuredWidth(scrap);
            int perItemHeight = getDecoratedMeasuredHeight(scrap);

            layoutDecorated(scrap, 0, offsetY, perItemWidth, offsetY + perItemHeight);
            offsetY += perItemHeight;
        }

        mTotalHeight = offsetY;
    }
}
