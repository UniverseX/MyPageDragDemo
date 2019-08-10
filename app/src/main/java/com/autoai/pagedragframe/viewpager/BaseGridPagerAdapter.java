package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGridPagerAdapter<V> extends BasePagerAdapter<RecyclerView, V> {
    private final int mRow;
    private final int mColumn;
    private List<List<V>> mPageData = new ArrayList<>();
    public BaseGridPagerAdapter(Context context, int row, int column, List<V> list) {
        super(context, list);
        mRow = row;
        mColumn = column;
        updateAllPageData(list);
    }

    @Override
    public void reCreateAllPages(List<V> list){
        updateAllPageData(list);

        super.reCreateAllPages(list);
    }

    protected void updateAllPageData(List<V> list) {
        mPageData.clear();
        int pageNum = getPageNum(list);
        for (int i = 0; i < pageNum; i++) {
            mPageData.add(getPageInfo(i, list));
        }
    }

    protected void updatePageData(int pageIndex, List<V> subList) {
        mPageData.remove(pageIndex);
        mPageData.add(pageIndex, subList);
    }

    /**
     * @param fromListIndex the index in getAllData()
     * @param toListIndex the index in getAllData()
     * @see #transToDataListIndex(int, int)
     * @see #getAllData()
     * @see #notifyPageChanged(int)
     */
    public void switchPageItem(int fromListIndex, int toListIndex) {
        List<V> allData = getAllData();
        V remove = allData.remove(fromListIndex);
        allData.add(toListIndex, remove);
        updateAllPageData(allData);
    }

    @Override
    public RecyclerView onCreatePage(ViewGroup parent) {
        return new RecyclerView(parent.getContext());
    }

    @Override
    public void onBindPage(Context context, RecyclerView recyclerView, int pageIndex) {
        recyclerView.setLayoutManager(getLayoutManager(context));
        recyclerView.setAdapter(generateItemAdapter(getPageInfo(pageIndex), pageIndex));
    }

    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new NoVerticalGridManager(context, mColumn, LinearLayoutManager.VERTICAL, false);
    }

    private class NoVerticalGridManager extends GridLayoutManager {

        public NoVerticalGridManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            setRecycleChildrenOnDetach(true);
        }

        public NoVerticalGridManager(Context context, int spanCount) {
            super(context, spanCount);
            setRecycleChildrenOnDetach(true);
        }

        public NoVerticalGridManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
            super(context, spanCount, orientation, reverseLayout);
            setRecycleChildrenOnDetach(true);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }

    @Override
    public void release() {
        int pageNum = getPageNum();
        for (int i = 0; i < pageNum; i++) {
            RecyclerView page = getPage(i);
            RecyclerView.LayoutManager layoutManager = page.getLayoutManager();
            if(layoutManager instanceof LinearLayoutManager){
                ((LinearLayoutManager) layoutManager).setRecycleChildrenOnDetach(false);
            }
        }

        super.release();
    }

    protected abstract GridRecycleAdapter generateItemAdapter(List<V> data, int pageIndex);

    @Override
    public int getPageNum() {
        return mPageData.size();
    }

    private int getPageNum(List<V> list) {
        return list.size() / getPageContentSize() + ((list.size() % getPageContentSize() == 0) ? 0 : 1);
    }

    @Override
    public List<V> getAllData() {
        ArrayList<V> vs = new ArrayList<>();
        final int size = mPageData.size();
        for (int i = 0; i < size; i++) {
            vs.addAll(mPageData.get(i));
        }
        return vs;
    }

    public void reBindAllPage(){
        for (int i = 0; i < getCount(); i++) {
            notifyPageChanged(i);
        }
    }

    public void notifyPageChanged(int pageIndex){
        if(pageIndex < 0 || pageIndex >= getCount()){
            return;
        }
        RecyclerView recyclerView = getPage(pageIndex);
        if(recyclerView.getAdapter() != null){
            GridRecycleAdapter adapter = (GridRecycleAdapter) recyclerView.getAdapter();
            adapter.updateData(getPageInfo(pageIndex));
            adapter.notifyDataSetChanged();
        }
    }

    public int getPageContentSize(){
        return mColumn * mRow;
    }

    @CallSuper
    public void addPageData(V v){
        List<V> lastPageList = mPageData.get(mPageData.size() - 1);
        if(lastPageList.size() == getPageContentSize()){
            //prepare data
            ArrayList<V> newPageList = new ArrayList<>();
            newPageList.add(v);
            mPageData.add(newPageList);
            //create and add page
            addPage(createPage(getPage(0).getContext()));
        }else {
            lastPageList.add(v);

            RecyclerView recyclerView = getPage(mPageData.size() - 1);
            if(recyclerView.getAdapter() != null){
                GridRecycleAdapter adapter = (GridRecycleAdapter) recyclerView.getAdapter();
                adapter.updateData(lastPageList);
                adapter.notifyItemInserted(lastPageList.size() - 1);
            }
        }
    }

    @CallSuper
    public void removePageData(V v){
        int oldPageNum = getPageNum();
        List<V> allData = getAllData();
        boolean result = allData.remove(v);
        if(result) {
            updateAllPageData(allData);
            if(oldPageNum != getPageNum()){
                //del page
                removePage(oldPageNum - 1);
            }else {
                reBindAllPage();
            }
        }
    }

    @CallSuper
    public void updatePageData(V v){
        List<V> allData = getAllData();
        int pageContentSize = getPageContentSize();
        for (int i = 0; i < allData.size(); i++) {
            if(updateEqualJudge(allData.get(i), v)){
                int pageIndex = i / pageContentSize;
                int pageInnerPosition = i % pageContentSize;
                RecyclerView page = getPage(pageIndex);
                RecyclerView.Adapter adapter = page.getAdapter();
                if(adapter != null){
                    adapter.notifyItemChanged(pageInnerPosition);
                }
                return;
            }
        }
    }

    protected boolean updateEqualJudge(V v, V v1) {
        return v == v1;
    }


    public List<V> getPageInfo(int pageIndex) {
        return mPageData.get(pageIndex);
    }

    private List<V> getPageInfo(int pageIndex, List<V> vList) {
        int startIndex = pageIndex * getPageContentSize();
        int pageNum = getPageNum(vList);
        int endInfoIndex = (pageIndex == pageNum - 1) ? vList.size() : (pageIndex + 1) * getPageContentSize();
        return new ArrayList<>(vList.subList(startIndex, endInfoIndex));
    }

    public int transToDataListIndex(int pageIndex, int childIndex) {
        if(pageIndex < 0 || childIndex < 0){
            return -1;
        }
        return pageIndex * getPageContentSize() + childIndex;
    }

    public int getPageItemPosition(int pageIndex, int allDataIndex){
        return allDataIndex - pageIndex * getPageContentSize();
    }

    public int getColumn() {
        return mColumn;
    }

    public int getRow() {
        return mRow;
    }

    public abstract class GridRecycleAdapter<VH extends PageViewHolder> extends RecyclerView.Adapter<VH>{
        protected List<V> data = new ArrayList<>();
        protected int mPageIndex;
        public GridRecycleAdapter(List<V> list, int pageIndex){
            this.mPageIndex = pageIndex;
            updateData(list);
        }

        public void updateData(List<V> list){
            data.clear();
            data.addAll(list);

            updatePageData(mPageIndex, list);
            notifyDataSetChanged();
        }

        public List<V> getData() {
            return new ArrayList<>(data);
        }

        public V getValue(int position){
            return data.get(position);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return onCreateViewHolder(parent, viewType, mPageIndex);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            onBindViewHolder(holder, position, mPageIndex);
        }

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType, int pageIndex);
        public abstract void onBindViewHolder(VH holder, int position, int pageIndex);

    }

    public static class PageViewHolder extends RecyclerView.ViewHolder{
        public int pageIndex;
        public PageViewHolder(View itemView, int pageIndex) {
            super(itemView);
            this.pageIndex = pageIndex;
        }
    }
}
