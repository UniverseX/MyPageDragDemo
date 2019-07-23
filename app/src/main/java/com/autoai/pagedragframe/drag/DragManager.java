package com.autoai.pagedragframe.drag;

import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;


public class DragManager<V extends View> {

    private SparseArray<DragListenerDispatcher<V, DragInfo>> mListeners = new SparseArray<>();

    public void startDrag(View itemView, DragInfo dragInfo){
        DragListenerDispatcher<V, DragInfo> dragListener = getDragListener(dragInfo.pageIndex);
        if(dragListener != null) {
            PointF touchPoint = dragListener.getLastTouchPoint();
            int x = (int) (touchPoint.x - itemView.getX());
            int y = (int) (touchPoint.y - itemView.getY());
            startDrag(itemView, dragInfo, new NoForegroundShadowBuilder(itemView, new Point(x, y)));
        }else {
            startDrag(itemView, dragInfo, null);
        }
    }

    public void startDrag(View itemView, DragInfo dragInfo, View.DragShadowBuilder dragShadowBuilder) {
        try {
            if(dragShadowBuilder == null) {
                dragShadowBuilder = new View.DragShadowBuilder(itemView);
            }

            Point shadowSize = new Point();
            Point shadowTouchPoint = new Point();
            dragShadowBuilder.onProvideShadowMetrics(shadowSize, shadowTouchPoint);
            dragInfo.shadowSize.set(shadowSize.x, shadowSize.y);
            dragInfo.shadowTouchPoint.set(shadowTouchPoint.x, shadowTouchPoint.y);
            dragInfo.draggingView = itemView;

            itemView.startDrag(null, dragShadowBuilder,
                    dragInfo, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDragListener(int key, DragListenerDispatcher<V, DragInfo> l) {
        mListeners.put(key, l);
    }

    public DragListenerDispatcher<V, DragInfo> getDragListener(int key){
        return mListeners.get(key);
    }

    public void removeDragListener(int key) {
        mListeners.remove(key);
    }

    public void clearListeners() {
        mListeners.clear();
    }

    public long getDraggingId(int key) {
        DragListenerDispatcher<V, DragInfo> dragListener = getDragListener(key);
        return dragListener == null ? RecyclerView.NO_ID : dragListener.getDraggingId();
    }
}
