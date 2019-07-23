package com.autoai.pagedragframe.bean;

public interface DataSetObserver<E> {
    void onAddAll(boolean result);
    void onClear();
    void onRemoveAll();
    void onAdd(boolean result);
    void onAdd(int index, E element);

    void onRemove(boolean result);
    void onRemove(E remove);
}
