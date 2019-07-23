package com.autoai.pagedragframe.drag;

import android.graphics.PointF;
import android.view.DragEvent;
import android.view.View;

import java.lang.ref.WeakReference;

public abstract class DragListenerDispatcher<V extends View, T extends DragInfo> implements View.OnDragListener {
    protected final WeakReference<V> viewRef;
    protected DragManager mDragManager;
    public DragListenerDispatcher(V v){
        viewRef = new WeakReference<>(v);
    }

    public DragListenerDispatcher(V v, DragManager dragManager){
        viewRef = new WeakReference<>(v);
        this.mDragManager = dragManager;
    }

    public void attachDragManager(DragManager dragManager){
        this.mDragManager = dragManager;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        if (view != viewRef.get() || !(event.getLocalState() instanceof DragInfo)) {
            return false;
        }

        T dragInfo = (T)event.getLocalState();
        V v = (V) view;

        if (!onDragPrepare(dragInfo, v)) {
            return false;
        }

        dragInfo.dragX = event.getX();
        dragInfo.dragY = event.getY();

        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                onDragStart(dragInfo, v);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                onDragEnd(dragInfo, v);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                onDragEnter(dragInfo, v);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                onDragExit(dragInfo, v);
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                onDragOver(dragInfo, v);
                break;
            case DragEvent.ACTION_DROP:
//                if (acceptDrop(dragInfo, v)) {
                    onDrop(dragInfo, v);
//                }
                break;
        }
        return true;
    }

    /**
     * need check param type
     * @param v your listener view
     */
    public abstract boolean onDragPrepare(T dragInfo, V v);

    public abstract void onDragStart(T dragInfo, V v);

    public abstract void onDragEnd(T dragInfo, V v);

    public abstract void onDrop(T dragInfo, V v);

    public abstract void onDragEnter(T dragInfo, V v);

    public abstract void onDragOver(T dragInfo, V v);

    public abstract void onDragExit(T dragInfo, V v);

    public abstract boolean acceptDrop(T dragInfo, V v);

    public abstract long getDraggingId();

    public abstract PointF getLastTouchPoint();

    public abstract void clearMove();

    public abstract void onPageTransfer(T lastDragInfo, T dragInfo);
}
