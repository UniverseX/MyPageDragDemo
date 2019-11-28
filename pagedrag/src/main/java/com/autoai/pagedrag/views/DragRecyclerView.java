package com.autoai.pagedrag.views;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.autoai.pagedrag.bean.DragInfo;

import static java.lang.Float.MIN_VALUE;

public class DragRecyclerView extends RecyclerView implements DragViewPager.DragListener, RecyclerView.OnItemTouchListener {
    private final PointF lastTouchPoint = new PointF(); // used to create ShadowBuilder
    private final PointF nextMoveTouchPoint = new PointF(MIN_VALUE, MIN_VALUE);
    private RecyclerDragListener recyclerDragListener;

    public DragRecyclerView(Context context) {
        this(context, null);
    }

    public DragRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnItemTouchListener(this);
    }

    @Override
    public int[] getVisualCenterCoordinate(DragEvent event) {
        Object localState = event.getLocalState();
        if (!(localState instanceof DragInfo)) {
            return null;
        }
        DragInfo dragInfo = (DragInfo) localState;
        int[] coordinate = new int[2];

        coordinate[0] = (int) (event.getX() - dragInfo.shadowTouchPoint.x + dragInfo.shadowSize.x / 2f);
        coordinate[1] = (int) (event.getY() - dragInfo.shadowTouchPoint.y + dragInfo.shadowSize.y / 2f);
        return coordinate;
    }

    @Override
    public void onDragStart(View v, DragEvent event) {
        Object localState = event.getLocalState();
        if (!(localState instanceof DragInfo)) {
            return;
        }
        if (recyclerDragListener != null) {
            final long itemId = ((DragInfo) localState).itemId;
            v.post(new Runnable() {
                @Override
                public void run() {
                    recyclerDragListener.onDragStart(itemId, DragRecyclerView.this);
                }
            });
        }
    }

    @Override
    public void onDragOver(View v, DragEvent event, final int[] coordinate) {
        Object localState = event.getLocalState();
        if (!(localState instanceof DragInfo)) {
            return;
        }
        if (recyclerDragListener != null) {
            final long itemId = ((DragInfo) localState).itemId;
            v.post(new Runnable() {
                @Override
                public void run() {
                    final int x = coordinate[0];
                    final int y = coordinate[1];
                    final int fromPosition = recyclerDragListener.getPositionForId(itemId);
                    int toPosition = -1;
                    View view = findChildViewUnder(x, y);
                    if (view != null) {
                        toPosition = getChildAdapterPosition(view);
                    }

                    if (toPosition >= 0 && fromPosition != toPosition) {
                        boolean scheduleNextMove = nextMoveTouchPoint.equals(MIN_VALUE, MIN_VALUE);
                        nextMoveTouchPoint.set(x, y);
                        if (scheduleNextMove) {
                            getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                                @Override
                                public void onAnimationsFinished() {
                                    if (nextMoveTouchPoint.equals(MIN_VALUE, MIN_VALUE)) {
                                        return;
                                    }

                                    final int fromPosition = recyclerDragListener.getPositionForId(itemId);

                                    View child = findChildViewUnder(nextMoveTouchPoint.x, nextMoveTouchPoint.y);
                                    if (child != null) {
                                        final int newToPosition = getChildAdapterPosition(child);
                                        DragRecyclerView.this.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                recyclerDragListener.onMove(fromPosition, newToPosition);
                                            }
                                        });
                                    }

                                    // reset so we know to schedule listener again next time
                                    clearMove();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDragEnter(View v, DragEvent event, final DragViewPager.DragListener lastListener, final int vectorOutZone, final int[] coordinate) {
        Object localState = event.getLocalState();
        if (!(localState instanceof DragInfo)) {
            return;
        }
        if (recyclerDragListener != null) {
            final long itemId = ((DragInfo) localState).itemId;
            v.post(new Runnable() {
                @Override
                public void run() {
                    if (!(lastListener instanceof DragRecyclerView)) {
                        return;
                    }
                    DragRecyclerView lastPage = (DragRecyclerView) lastListener;
                    recyclerDragListener.onDragEnter(itemId, DragRecyclerView.this, lastPage, vectorOutZone, coordinate);

                    RecyclerDragListener lastRecyclerDragListener = lastPage.getRecyclerDragListener();
                    if (lastRecyclerDragListener == null) {
                        return;
                    }

                    //1.获取下一个page与draggingItem坐标最近的item
                    View childViewUnder = findChildViewUnder(coordinate[0] - vectorOutZone, coordinate[1]);//horizontal
                    int lastPosition = recyclerDragListener.getLastAdapterPosition();
                    if (childViewUnder == null) {
                        childViewUnder = DragRecyclerView.this.findViewHolderForAdapterPosition(lastPosition).itemView;
                    }
                    if (childViewUnder != null) {
                        int childAdapterPosition = getChildAdapterPosition(childViewUnder);
                        //2.last Page删除draggingItem
                        Object draggingItem = lastRecyclerDragListener.removePosition(lastRecyclerDragListener.getPositionForId(itemId));
                        //3.new page删除item
                        Object pendingMoveToLast = recyclerDragListener.removePosition(vectorOutZone < 0 ? lastPosition : 0);
                        //4.new page添加draggingItem
                        recyclerDragListener.addItemToPosition(childAdapterPosition, draggingItem);
                        //5.last page添加新item到adapter中
                        lastRecyclerDragListener.addItemToPosition(-1, pendingMoveToLast);
                    }
                }
            });
        }
    }

    @Override
    public void onDragExit(View v, DragEvent event, final DragViewPager.DragListener nextListener, final int vectorOutZone, final int[] coordinate) {
        Object localState = event.getLocalState();
        if (!(localState instanceof DragInfo)) {
            return;
        }
        if (recyclerDragListener != null) {
            final long itemId = ((DragInfo) localState).itemId;
            v.post(new Runnable() {
                @Override
                public void run() {
                    recyclerDragListener.onDragExit(itemId, DragRecyclerView.this, (RecyclerView) nextListener, vectorOutZone, coordinate);
                }
            });
        }
    }

    @Override
    public void onDrop(View v, DragEvent event, final int[] coordinate) {
    }

    @Override
    public void onDragEnd(View v, DragEvent event, final int[] coordinate) {
        Object localState = event.getLocalState();
        if (!(localState instanceof DragInfo)) {
            return;
        }
        if (recyclerDragListener != null) {
            final long itemId = ((DragInfo) localState).itemId;
            // queue up the show animation until after all move animations are finished
            getItemAnimator().isRunning(new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                @Override
                public void onAnimationsFinished() {
                    int position = recyclerDragListener.getPositionForId(itemId);

                    RecyclerView.ViewHolder vh = findViewHolderForItemId(itemId);
                    if (vh != null && vh.getAdapterPosition() != position) {
                        // if positions don't match, there's still an outstanding move animation
                        // so we try to reschedule the notifyItemChanged until after that
                        DragRecyclerView.this.getItemAnimator().isRunning(
                                new RecyclerView.ItemAnimator.ItemAnimatorFinishedListener() {
                                    @Override
                                    public void onAnimationsFinished() {
                                        DragRecyclerView.this.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                recyclerDragListener.onDragEnd(itemId, DragRecyclerView.this, coordinate);
                                            }
                                        });

                                    }
                                });

                    } else {
                        DragRecyclerView.this.post(new Runnable() {
                            @Override
                            public void run() {
                                recyclerDragListener.onDragEnd(itemId, DragRecyclerView.this, coordinate);
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        lastTouchPoint.set(e.getX(), e.getY());
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public void clearMove() {
        nextMoveTouchPoint.set(MIN_VALUE, MIN_VALUE);
    }

    public PointF getLastTouchPoint() {
        return lastTouchPoint;
    }

    public void setRecyclerDragListener(RecyclerDragListener recyclerDragListener) {
        this.recyclerDragListener = recyclerDragListener;
    }

    public RecyclerDragListener getRecyclerDragListener() {
        return recyclerDragListener;
    }

    public interface RecyclerDragListener<Data> {
        int getPositionForId(long draggingItemId);

        Data removePosition(int position);

        void addItemToPosition(int position, Data object);

        int getLastAdapterPosition();

        void onDragStart(long draggingItemId, RecyclerView recyclerView);

        void onMove(int fromPosition, int toPosition);

        void onDragEnter(long draggingItemId, RecyclerView currentRecyclerView, RecyclerView lastRecyclerView, int vectorOutZone, int[] coordinate);

        void onDragExit(long draggingItemId, RecyclerView currentRecyclerView, RecyclerView nextRecyclerView, int vectorOutZone, int[] coordinate);

        void onDragEnd(long draggingItemId, RecyclerView dragRecyclerView, int[] coordinate);
    }
}
