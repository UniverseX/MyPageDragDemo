package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class BaseViewPager extends ViewPager {


    public BaseViewPager(@NonNull Context context) {
        this(context, null);
    }

    public BaseViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(getAdapter() instanceof BasePagerAdapter){
            ((BasePagerAdapter) getAdapter()).release();
        }
    }

}
