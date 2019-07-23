package com.autoai.pagedragframe.drag;

import android.graphics.Point;
import android.view.View;

public class DragInfo {

    public float dragX = -1;
    public float dragY = -1;
    public long itemId = -1;
    public View draggingView;
    public int pageIndex = -1;

    public Point shadowSize = new Point();
    public Point shadowTouchPoint = new Point();
    public DragInfo(){}
    public DragInfo(DragInfo other){
        dragX = other.dragX;
        dragY = other.dragY;
        itemId = other.itemId;
        pageIndex= other.pageIndex;
        draggingView = other.draggingView;
        shadowSize.set(other.shadowSize.x, other.shadowSize.y);
        shadowTouchPoint.set(other.shadowTouchPoint.x, other.shadowTouchPoint.y);
    }

    public void reset(){
        dragX = -1;
        dragY = -1;
        itemId = -1;
        pageIndex= -1;
        draggingView = null;
    }
}