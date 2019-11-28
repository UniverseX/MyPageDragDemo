package com.autoai.pagedrag.views;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.Size;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.lang.ref.WeakReference;

public class DragViewPager extends RecyclerViewPager<DragViewPager.DragListener> implements View.OnDragListener {
    public static final int SCROLL_DELAY = 500;
    public static final int RESCROLL_DELAY = 900;

    private static final int SCROLL_OUTSIDE_ZONE = 0;
    private static final int SCROLL_WAITING_IN_ZONE = 1;

    static final int SCROLL_NONE = 0;
    static final int SCROLL_LEFT = -1;
    static final int SCROLL_RIGHT = 1;

    private ScrollRunnable mScrollRunnable;
    private Handler mHandler;
    private int leftOutZone;
    private int rightOutZone;
    private boolean mDragging;
    private int mScrollState = SCROLL_OUTSIDE_ZONE;
    private int mDistanceSinceScroll = 0;
    private int[] mLastTouch = new int[2];

    private DragListener lastDragListener;
    private int touchSlop;
    private boolean canPageScrollOnDragging = true;//是否可以跨页拖动

    public DragViewPager(Context context) {
        this(context, null);
    }

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        setOnDragListener(this);
        mHandler = new Handler();
        mScrollRunnable = new ScrollRunnable(this);
    }

    public void setLeftOutZone(int leftOutZone) {
        this.leftOutZone = leftOutZone;
    }

    public void setRightOutZone(int rightOutZone) {
        this.rightOutZone = rightOutZone;
    }

    public void setCanPageScrollOnDragging(boolean canPageScrollOnDragging) {
        this.canPageScrollOnDragging = canPageScrollOnDragging;
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    public boolean onDrag(final View v, final DragEvent event) {
        if (viewPagerHelper == null) {
            return false;
        }

        int currentItem = getCurrentItem();
        DragListener dragListener = viewPagerHelper.getCurrentItem(currentItem);
        if (dragListener == null) {
            return false;
        }

        int[] coordinate = dragListener.getVisualCenterCoordinate(event);
        if (coordinate == null) {
            return false;
        }

        int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                mDragging = true;

                final float outX = coordinate[0];
                boolean isLeftOut = outX < leftOutZone;
                if (isLeftOut || (outX > getWidth() - rightOutZone)) {
                    mScrollState = SCROLL_WAITING_IN_ZONE;
                    mScrollRunnable.setDirection(isLeftOut ? SCROLL_LEFT : SCROLL_RIGHT);
                    mHandler.postDelayed(mScrollRunnable, SCROLL_DELAY);
                } else {
                    mScrollState = SCROLL_OUTSIDE_ZONE;
                }

                dragListener.onDragStart(v, event);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                mDistanceSinceScroll = 0;
                if (mDragging) {
                    mDragging = false;
                    dragListener.onDragExit(v, event, null, 0, coordinate);
                    clearScrollRunnable();
                }

                if(lastDragListener != null && lastDragListener != dragListener){
                    lastDragListener.onDragEnd(v, event, coordinate);
                }
                dragListener.onDragEnd(v, event, coordinate);
                lastDragListener = null;
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                checkTouchMove(v, event, dragListener, coordinate);

                final int x = coordinate[0];
                final int y = coordinate[1];

                // Check if we are hovering over the scroll areas
                mDistanceSinceScroll += Math.hypot(mLastTouch[0] - x, mLastTouch[1] - y);
                mLastTouch[0] = x;
                mLastTouch[1] = y;
                checkScrollState(x, y);
                break;
            case DragEvent.ACTION_DROP:
                dragListener.onDrop(v, event, coordinate);
                break;
        }


        return true;
    }

    private void checkTouchMove(View v, DragEvent event, DragListener dragListener, int[] visualCenterCoordinate) {
        if (dragListener != null) {
            if (lastDragListener != dragListener) {
                int direction = mScrollRunnable.getDirection();
                if (lastDragListener != null) {
                    lastDragListener.onDragExit(v, event, dragListener, direction < 0 ? direction * leftOutZone : direction * rightOutZone, visualCenterCoordinate);
                }
                dragListener.onDragEnter(v, event, lastDragListener,  direction < 0 ? direction * leftOutZone : direction * rightOutZone, visualCenterCoordinate);
            }
            dragListener.onDragOver(v, event, visualCenterCoordinate);
        } else {
            if (lastDragListener != null) {
                lastDragListener.onDragExit(v, event, null, 0, visualCenterCoordinate);
            }
        }
        lastDragListener = dragListener;
    }

    private void clearScrollRunnable() {
        mHandler.removeCallbacks(mScrollRunnable);
        if (mScrollState == SCROLL_WAITING_IN_ZONE) {
            mScrollState = SCROLL_OUTSIDE_ZONE;
            mScrollRunnable.setDirection(SCROLL_NONE);
        }
    }

    private void checkScrollState(int x, int y) {
        final int delay = mDistanceSinceScroll < touchSlop ? RESCROLL_DELAY : SCROLL_DELAY;
        if (x < leftOutZone) {//Turn to the left page
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                mScrollRunnable.setDirection(SCROLL_LEFT);
                mHandler.postDelayed(mScrollRunnable, delay);
            }
        } else if (x > getWidth() - rightOutZone) {
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                mScrollRunnable.setDirection(SCROLL_RIGHT);
                mHandler.postDelayed(mScrollRunnable, delay);
            }
        } else {
            clearScrollRunnable();
        }
    }

    private static class ScrollRunnable implements Runnable {
        private int mDirection;
        private WeakReference<DragViewPager> weakReference;

        private ScrollRunnable(DragViewPager dragViewPager) {
            weakReference = new WeakReference<>(dragViewPager);
        }

        public void run() {
            DragViewPager viewPager = weakReference.get();
            if (viewPager != null) {
                if (viewPager.canPageScrollOnDragging) {
                    int currentItem = viewPager.getCurrentItem();
                    if (mDirection == SCROLL_LEFT) {
                        viewPager.setCurrentItem(currentItem - 1);
                    } else if (mDirection == SCROLL_RIGHT) {
                        viewPager.setCurrentItem(currentItem + 1);
                    }
                }
                viewPager.mScrollState = SCROLL_OUTSIDE_ZONE;
                viewPager.mDistanceSinceScroll = 0;

                if (viewPager.isDragging() && viewPager.canPageScrollOnDragging) {
                    // Check the scroll again so that we can requeue the scroller if necessary
                    viewPager.checkScrollState(viewPager.mLastTouch[0], viewPager.mLastTouch[1]);
                }
            }
        }

        void setDirection(int direction) {
            mDirection = direction;
        }

        public int getDirection() {
            return mDirection;
        }
    }

    public boolean isDragging() {
        return mDragging;
    }

    public interface DragListener {
        @Size(value = 2)
        int[] getVisualCenterCoordinate(DragEvent event);

        void onDragStart(View v, DragEvent event);

        void onDragOver(View v, DragEvent event, int[] coordinate);

        void onDragEnter(View v, DragEvent event, DragListener lastListener, int vectorOutZone, int[] coordinate);

        void onDragExit(View v, DragEvent event, DragListener nextListener, int vectorOutZone, int[] coordinate);

        void onDrop(View v, DragEvent event, int[] coordinate);

        void onDragEnd(View v, DragEvent event, int[] coordinate);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        touchSlop = ViewConfiguration.get(getContext()).getScaledWindowTouchSlop();
    }
}
