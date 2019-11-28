package com.autoai.pagedrag.shadowbuilder;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

/**
 * ViewShadowBuilder scale from center
 */
public class ScaleViewShadowBuilder extends DragSortShadowBuilder {
    private static final String TAG = "ScaleViewShadowBuilder";
    private final Matrix matrix = new Matrix();
    private float sx = 1;
    private float sy = 1;
    public ScaleViewShadowBuilder(View view, Point touchPoint) {
        super(view, touchPoint);
    }

    public void setScale(float sx, float sy){
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void onProvideShadowMetrics(@NonNull Point shadowSize, @NonNull Point shadowTouchPoint) {
        final View view = getView();
        if (view != null) {
            shadowSize.set((int) (view.getWidth() * this.sx), (int) (view.getHeight() * this.sy));
            Point touchPoint = getTouchPoint();
            shadowTouchPoint.set(touchPoint.x, touchPoint.y);
        } else {
            Log.e(TAG, "Asked for drag thumb metrics but no view");
        }
    }
    @Override
    public void onDrawShadow(@NonNull Canvas canvas) {
        View view = getView();
        if(view != null){
            float pw = view.getWidth() * this.sx / 2f;
            float ph = view.getHeight() * this.sy / 2f;
            matrix.setScale(sx, sy, pw, ph);
            canvas.setMatrix(matrix);
            view.draw(canvas);
        }else {
            Log.e(TAG, "Asked to draw drag shadow but no view");
        }
    }
}
