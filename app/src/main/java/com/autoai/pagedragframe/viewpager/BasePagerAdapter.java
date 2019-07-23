package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePagerAdapter<Page extends View, Value> extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private List<ViewGroup> pages = new ArrayList<>();

    private final List<Value> mData;

    private WeakReference<Context> mContextRef;

    public BasePagerAdapter(Context context, List<Value> list) {
        mContextRef = new WeakReference<>(context);
        mData = new ArrayList<>(list);
    }

    @Override
    public int getCount() {
        if(pages.isEmpty() && !mData.isEmpty()){
            Context context = mContextRef.get();
            if(context != null) {
                initPages(context);
            }
        }
        return pages.size();
    }

    public void updateAll(List<Value> list){
        mData.clear();
        pages.clear();
        //remove views
        notifyDataSetChanged();

        mData.addAll(list);
        //reCreate views
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup viewGroup = pages.get(position);
        if (viewGroup.getParent() == null) {
            container.addView(viewGroup);
        }
        View childAt = viewGroup.getChildAt(0);
        if (childAt instanceof ViewGroup && ((ViewGroup)childAt).getChildCount() == 0) {
            onBindPage(container.getContext(), (Page)childAt, position);
        }
        return viewGroup;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    protected void initPages(Context context) {
        int pageNum = getPageNum();
        for (int i = 0; i < pageNum; i++) {
            pages.add(createPage(context));
        }
    }

    private FrameLayout createPage(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        Page page = onCreatePage(frameLayout);
        frameLayout.addView(page, generatePageLayoutParams());
        return frameLayout;
    }

    public FrameLayout.LayoutParams generatePageLayoutParams(){
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * @param parent is FrameLayout
     */
    public abstract Page onCreatePage(ViewGroup parent);

    /**
     * @param page is your created view
     */
    public abstract void onBindPage(Context context, Page page, int pageIndex);

    protected List<Value> getAllData() {
        return mData;
    }

    public ViewGroup getPage(int index) {
        return pages.get(index);
    }

    public int getPageNum() {
        return mData.size();
    }

    public void addPage(ViewGroup page) {
        pages.add(page);
        notifyDataSetChanged();
    }

    public void removePage(ViewGroup page) {
        if(pages.remove(page)) {
            notifyDataSetChanged();
        }
    }

    public int indexOfPage(ViewGroup viewGroup) {
        return pages.indexOf(viewGroup);
    }

    protected void emptyPages(){
        pages.clear();
        notifyDataSetChanged();
    }

    public void release() {
//        observers.clear();
        emptyPages();
    }

/*
    // TODO: 19-7-23 data observers
    private List<DataSetObserver<Value>> observers = new ArrayList<>();

    public void registerDataObserver(@NonNull DataSetObserver<Value> observer){
        observers.add(observer);
    }

    public void unregisterDataObserver(DataSetObserver<Value> observer){
        observers.remove(observer);
    }*/
}
