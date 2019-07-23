package com.autoai.pagedragframe.drag;


import android.view.View;

public interface DragNotifier {
    void onDragStart(int position, View draggingView);

    void onDragEnd(int positionForId, View draggingView);

    void onDragEnter(int fromPosition, View lastView);

    void onDragExit(int fromPosition, View lastView);

    void onDrop(long itemId, View draggingView);

    void onMove(int fromPosition, int toPosition);

    int getPositionForId(long itemId);

    long getItemId(int position);

    void onPageTransfer(DragInfo lastInfo, DragInfo newInfo);
}
