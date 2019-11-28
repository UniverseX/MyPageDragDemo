package com.autoai.pagedrag.bean;

import java.util.List;

public interface DataComparator<Data> {
    boolean areItemsTheSame(Data oldData, Data newData);

    boolean areContentsTheSame(Data oldData, Data newData);

    Object getChangePayload(Data oldData, Data newData);

    int getDataPosition(List<Data> allData, Data newData);
}