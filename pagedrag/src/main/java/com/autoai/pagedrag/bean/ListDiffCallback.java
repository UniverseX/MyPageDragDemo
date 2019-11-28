package com.autoai.pagedrag.bean;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class ListDiffCallback<Data> extends DiffUtil.Callback {
    private List<Data> oldList;
    private List<Data> newList;
    private DataComparator<Data> dataComparator;

    public ListDiffCallback(List<Data> oldList, List<Data> newList, DataComparator<Data> dataComparator) {
        this.oldList = oldList;
        this.newList = newList;
        this.dataComparator = dataComparator;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return dataComparator.areItemsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return dataComparator.areContentsTheSame(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return dataComparator.getChangePayload(oldList.get(oldItemPosition), newList.get(newItemPosition));
    }
}