package com.autoai.pagedragframe.viewpager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/*
todo recycle page items
 */
public abstract class BaseGridPagerAdapter<V> extends BasePagerAdapter<RecyclerView, V> {
    private final int mRow;
    private final int mColumn;
    private List<List<V>> mPageData = new ArrayList<>();
    public BaseGridPagerAdapter(Context context, int row, int column, List<V> list) {
        super(context, list);
        mRow = row;
        mColumn = column;
        updatePageData(list);
    }

    @Override
    public void updateAll(List<V> list){
        updatePageData(list);

        super.updateAll(list);
    }

    protected void updatePageData(List<V> list) {
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
        updatePageData(allData);
    }

    @Override
    public RecyclerView onCreatePage(ViewGroup parent) {
        return new RecyclerView(parent.getContext());
    }

    @Override
    public FrameLayout.LayoutParams generatePageLayoutParams() {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onBindPage(Context context, RecyclerView recyclerView, int pageIndex) {
        recyclerView.setLayoutManager(getLayoutManager(context));
        recyclerView.setAdapter(generateItemAdapter(getPageInfo(pageIndex), pageIndex));
    }

    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new GridLayoutManager(context, mColumn, LinearLayoutManager.VERTICAL, false);
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
    protected List<V> getAllData() {
        ArrayList<V> vs = new ArrayList<>();
        for (int i = 0; i < mPageData.size(); i++) {
            vs.addAll(mPageData.get(i));
        }
        return vs;
    }

    public void reBindPages(){
        for (int i = 0; i < getCount(); i++) {
            notifyPageChanged(i);
        }
    }

    public void notifyPageChanged(int pageIndex){
        if(pageIndex < 0 || pageIndex >= getCount()){
            return;
        }
        RecyclerView recyclerView = (RecyclerView) getPage(pageIndex).getChildAt(0);
        if(recyclerView.getAdapter() != null){
            GridRecycleAdapter adapter = (GridRecycleAdapter) recyclerView.getAdapter();
            adapter.updateData(getPageInfo(pageIndex));
            adapter.notifyDataSetChanged();
        }
    }

    public int getPageContentSize(){
        return mColumn * mRow;
    }

    public List<V> getPageInfo(int pageIndex) {
        return mPageData.get(pageIndex);
    }

    private List<V> getPageInfo(int pageIndex, List<V> vList) {
        int startIndex = pageIndex * getPageContentSize();
        int pageNum = getPageNum(vList);
        int endInfoIndex = (pageIndex == pageNum - 1) ? vList.size() : (pageIndex + 1) * getPageContentSize();
        return vList.subList(startIndex, endInfoIndex);
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
