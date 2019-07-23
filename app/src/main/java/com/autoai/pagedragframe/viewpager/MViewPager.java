package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class MViewPager extends ViewPager{


    public MViewPager(@NonNull Context context) {
        this(context, null);
    }

    public MViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);
        if(adapter instanceof OnPageChangeListener){
            addOnPageChangeListener((OnPageChangeListener) adapter);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(getAdapter() instanceof BasePagerAdapter){
            ((BasePagerAdapter) getAdapter()).release();
        }
    }

}
