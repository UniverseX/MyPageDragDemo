package com.autoai.pagedrag.bean;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PageData<Data> implements DataComparator<Data> {
    private final int mRow;
    private final int mColumn;
    private final int mPageContentSize;//每一页的数量
    protected int pageNum;//几个页面
    private List<List<Data>> mPageData = new ArrayList<>();
    protected List<Data> allData;
    private DataObserver<Data> dataObserver;

    public PageData(int row, int column, List<Data> list) {
        mRow = row;
        mColumn = column;
        mPageContentSize = row * column;

        pageNum = getPageNum(list);
        allData = new ArrayList<>(list);

        //分配data
        updatePagesData();
    }

    protected int getPageNum(List<Data> list) {
        return list.size() / mPageContentSize + ((list.size() % mPageContentSize == 0) ? 0 : 1);
    }

    public int getPageNum() {
        return mPageData.size();
    }

    protected void updatePagesData() {
        mPageData.clear();
        for (int i = 0; i < pageNum; i++) {
            mPageData.add(getSinglePageData(i));
        }
    }

    private List<Data> getSinglePageData(int pageIndex) {
        int startIndex = pageIndex * mPageContentSize;
        int endInfoIndex = (pageIndex == pageNum - 1) ? allData.size() : (pageIndex + 1) * mPageContentSize;
        return new ArrayList<>(allData.subList(startIndex, endInfoIndex));
    }

    @NonNull
    public List<Data> getPageData(int pageIndex) {
        return pageIndex >= mPageData.size() ? new ArrayList<Data>() : mPageData.get(pageIndex);
    }

    public List<Data> getAllData() {
        return allData;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getRow() {
        return mRow;
    }

    public int getPageContentSize() {
        return mPageContentSize;
    }

    public void setDataObserver(DataObserver<Data> dataObserver) {
        this.dataObserver = dataObserver;
    }

    public void insertData(Data data) {
        insertData(-1, data);
    }

    public void insertData(int listPosition, Data data) {
        List<List<Data>> oldPageData = new ArrayList<>(mPageData);

        if (listPosition < 0) {
            allData.add(data);
        } else {
            allData.add(listPosition, data);
        }
        pageNum = getPageNum(allData);
        updatePagesData();

        if (dataObserver == null) {
            return;
        }
        final int oldSize = oldPageData.size();
        for (int i = 0; i < pageNum; i++) {
            if (i < oldSize) {
                List<Data> newList = mPageData.get(i);
                dataObserver.notifyPageChanged(i, newList, this);
            } else {
                dataObserver.notifyPageAdd(i);
            }
        }
    }

    public void removeData(Data data) {
        List<List<Data>> oldPageData = new ArrayList<>(mPageData);

        allData.remove(data);
        pageNum = getPageNum(allData);
        updatePagesData();

        if (dataObserver == null) {
            return;
        }

        final int newSize = pageNum;
        final int oldSize = oldPageData.size();
        for (int i = 0; i < oldSize; i++) {
            if (i < newSize) {
                List<Data> newList = mPageData.get(i);
                dataObserver.notifyPageChanged(i, newList, this);
            } else {
                dataObserver.notifyPageRemoved(i);
            }
        }

    }

    /**
     * @param payload 有效更改点
     */
    public void updateData(Data data, Object payload) {
        if (dataObserver != null) {
            int dataPosition = getDataPosition(allData, data);
            if (dataPosition >= 0) {
                int pageIndex = dataPosition / mPageContentSize;
                int itemListPosition = dataPosition - pageIndex * mPageContentSize;

                dataObserver.notifyItemChanged(pageIndex, itemListPosition, payload);
            } else {
                insertData(data);
            }
        }
    }

    public void updateAll(List<Data> list) {
        List<List<Data>> oldPageData = new ArrayList<>(mPageData);

        allData.clear();
        allData.addAll(list);
        pageNum = getPageNum(allData);
        updatePagesData();

        if (dataObserver == null) {
            return;
        }

        final int oldSize = oldPageData.size();
        if (oldSize < pageNum) {
            for (int i = 0; i < pageNum; i++) {
                if (i < oldSize) {
                    List<Data> newList = mPageData.get(i);
                    dataObserver.notifyPageChanged(i, newList, this);
                } else {
                    dataObserver.notifyPageAdd(i);
                }
            }
        } else {
            final int newSize = pageNum;
            for (int i = 0; i < oldSize; i++) {
                if (i < newSize) {
                    List<Data> newList = mPageData.get(i);
                    dataObserver.notifyPageChanged(i, newList, this);
                } else {
                    dataObserver.notifyPageRemoved(i);
                }
            }
        }
    }

    public void updateAllDataByPage() {
        allData.clear();
        int size = mPageData.size();
        for (int i = 0; i < size; i++) {
            allData.addAll(mPageData.get(i));
        }
    }

    public interface DataObserver<Data> {
        void notifyPageAdd(int pageIndex);

        void notifyPageRemoved(int pageIndex);

        void notifyItemChanged(int pageIndex, int itemPosition, Object payload);

        void notifyPageChanged(int pageIndex, List<Data> newList, DataComparator<Data> dataComparator);
    }
}
