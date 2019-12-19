package com.autoai.pagedrag;

public interface ViewPagerHelper<T> extends IRelease {
    T getCurrentItem(int position);
}