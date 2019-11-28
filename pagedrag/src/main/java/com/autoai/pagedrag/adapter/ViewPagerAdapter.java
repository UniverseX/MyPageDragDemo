package com.autoai.pagedrag.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    private List<? extends View> views;

    public void init(List<? extends View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (views == null || views.size() == 0) {
            throw new RuntimeException("the views data set has not initialized yet, please call method init");
        }
        View view = views.get(position);
        if (view.getParent() == null) {
            container.addView(view);
            onViewAddToParent(view);
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        onViewRemoveFromParent((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        if(views.indexOf(object) < 0){
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    public void onViewAddToParent(View view) {
    }

    public void onViewRemoveFromParent(View view) {
    }
}
