package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;

public class RecycleViewPager extends BaseViewPager {
    public RecycleViewPager(@NonNull Context context) {
        super(context);
    }

    public RecycleViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * {@link BaseGridPagerAdapter}
     */
    public void release() {
        PagerAdapter adapter = getAdapter();
        if(adapter instanceof BasePagerAdapter){
            ((BasePagerAdapter) adapter).release();
        }
        removeAllViews();
    }

    public void restore() {
        PagerAdapter adapter = getAdapter();
        if(adapter instanceof BasePagerAdapter) {
            ((BasePagerAdapter) adapter).setContext(getContext());
            adapter.notifyDataSetChanged();
        }
    }
}
