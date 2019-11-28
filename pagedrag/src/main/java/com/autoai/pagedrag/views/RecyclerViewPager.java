package com.autoai.pagedrag.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import com.autoai.pagedrag.ViewPagerHelper;

public class RecyclerViewPager<T> extends ViewPager {
    protected ViewPagerHelper<T> viewPagerHelper;

    public RecyclerViewPager(Context context) {
        super(context);
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setViewPagerHelper(ViewPagerHelper<T> viewPagerHelper) {
        this.viewPagerHelper = viewPagerHelper;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (viewPagerHelper != null) {
            viewPagerHelper.release();
        }
        super.onDetachedFromWindow();
    }
}
