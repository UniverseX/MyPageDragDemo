package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePagerAdapter<Page extends View, Value> extends PagerAdapter {

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

    public void reCreateAllPages(List<Value> list){
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
        if(object instanceof ViewGroup && ((ViewGroup) object).getChildCount() > 0) {
            View childAt = ((ViewGroup) object).getChildAt(0);
            if (childAt instanceof ViewGroup) {
                onUnbindPage((Page)childAt, position);
            }
        }
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

    protected FrameLayout createPage(Context context) {
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

    public void onUnbindPage(Page view, int pageIndex){}

    protected List<Value> getAllData() {
        return mData;
    }

    public Page getPage(int index) {
        return (Page) pages.get(index).getChildAt(0);
    }

    public int getPageNum() {
        return mData.size();
    }

    public final int getPageSize(){
        return pages.size();
    }

    public void addPage(ViewGroup page) {
        pages.add(page);
        notifyDataSetChanged();
        if(pagesUpdateListener != null){
            pagesUpdateListener.onPageAdded(pages.size() - 1);
        }
    }

    public void removePage(int pageIndex) {
        if(pages.remove(pageIndex) != null){
            notifyDataSetChanged();
            if(pagesUpdateListener != null){
                pagesUpdateListener.onPageRemoved();
            }
        }
    }

    public int indexOfPage(ViewGroup viewGroup) {
        return pages.indexOf(viewGroup);
    }

    protected void emptyPages(){
        pages.clear();
        notifyDataSetChanged();
    }

    @CallSuper
    public void release() {
        mContextRef.clear();
        emptyPages();
    }

    public void setContext(Context context){
        mContextRef = new WeakReference<>(context);
    }

    public interface OnPagesUpdateListener {
        void onPageAdded(int pageIndex);
        void onPageRemoved();
    }
    private OnPagesUpdateListener pagesUpdateListener;

    public void setOnPageChangeListener(OnPagesUpdateListener pagesUpdateListener){
        this.pagesUpdateListener = pagesUpdateListener;
    }
}
