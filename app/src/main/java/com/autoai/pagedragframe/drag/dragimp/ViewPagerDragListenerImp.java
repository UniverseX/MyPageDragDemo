package com.autoai.pagedragframe.drag.dragimp;

import android.content.res.Resources;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewConfiguration;

import com.autoai.pagedragframe.drag.BaseDragPageAdapter;
import com.autoai.pagedragframe.drag.DragInfo;
import com.autoai.pagedragframe.drag.DragListenerDispatcher;

public class ViewPagerDragListenerImp extends DragListenerDispatcher<ViewPager, DragInfo> {
    private static final String TAG = "ViewPagerDragListener";
    private static final int SCROLL_DELAY = 600;
    private static final int RESCROLL_DELAY = 1000;
    private static final int SCROLL_NONE = -1;
    private static final int SCROLL_LEFT = 0;
    private static final int SCROLL_RIGHT = 1;
    private static final int SCROLL_OUTSIDE_ZONE = 0;
    private static final int SCROLL_WAITING_IN_ZONE = 1;

    private int mDistanceSinceScroll = 0;
    private int mLastTouch[] = new int[2];
    private Handler mHandler;
    private int leftOutZone;
    private int rightOutZone;
    private boolean mDragging;
    private DragListenerDispatcher mLastDragLisener;
    private int mScrollState = SCROLL_OUTSIDE_ZONE;
    private int shadowSizeX = 0;

    public ViewPagerDragListenerImp(ViewPager viewPager) {
        super(viewPager);
        mHandler = new Handler();
        leftOutZone = rightOutZone = (int) (Resources.getSystem().getDisplayMetrics().density * 100);
        mScrollRunnable = new ScrollRunnable(viewPager);
    }

    public void setLeftOutZone(int leftOutZone) {
        this.leftOutZone = leftOutZone;
    }

    public void setRightOutZone(int rightOutZone) {
        this.rightOutZone = rightOutZone;
    }

    @Override
    public boolean onDragPrepare(DragInfo dragInfo, ViewPager viewPager) {
        return true;
    }

    @Override
    public void onDragStart(DragInfo dragInfo, ViewPager viewPager) {
        mDragging = true;

        mLastDragLisener = null;

        shadowSizeX = dragInfo.shadowSize.x;

        final float x = dragInfo.dragX;
        final float y = dragInfo.dragY;

        final float visualCenterX = x - dragInfo.shadowTouchPoint.x + dragInfo.shadowSize.x / 2f;
//        final float visualCenterY = y - dragInfo.shadowTouchPoint.y + dragInfo.shadowSize.y / 2f;

        if (((visualCenterX - shadowSizeX / 4f) < leftOutZone) || ((visualCenterX + shadowSizeX / 4f) > viewPager.getWidth() - rightOutZone)) {
            mScrollState = SCROLL_WAITING_IN_ZONE;
            mHandler.postDelayed(mScrollRunnable, SCROLL_DELAY);
        } else {
            mScrollState = SCROLL_OUTSIDE_ZONE;
        }

//        //item
        DragListenerDispatcher dragListener = getDragListener(dragInfo.pageIndex, viewPager);
        if (dragListener != null) {
            dragListener.onDragStart(dragInfo, getPageView(dragInfo.pageIndex, viewPager));
        }
    }

    @Nullable
    private DragListenerDispatcher getDragListener(int pageIndex, ViewPager viewPager) {
        DragListenerDispatcher dragListener = mDragManager.getDragListener(pageIndex);
        if (viewPager.getCurrentItem() == pageIndex) {
            if (dragListener != null) {
                return dragListener;
            }
        }
        return null;
    }

    @Override
    public void onDragEnd(DragInfo dragInfo, ViewPager viewPager) {
        mDistanceSinceScroll = 0;
        if (mDragging) {
            mDragging = false;
            if (mLastDragLisener != null) {
                mLastDragLisener.onDragExit(dragInfo, getPageView(dragInfo.pageIndex, viewPager));
            }
            clearScrollRunnable();
        }

        DragListenerDispatcher dragListener = getDragListener(dragInfo.pageIndex, viewPager);
        if (dragListener != null) {
            dragListener.onDragEnd(dragInfo, getPageView(dragInfo.pageIndex, viewPager));
        }
    }

    @Override
    public void onDrop(DragInfo dragInfo, ViewPager viewPager) {
        DragListenerDispatcher dragListener = getDragListener(dragInfo.pageIndex, viewPager);
        if (dragListener != null) {
            dragListener.onDrop(dragInfo, getPageView(dragInfo.pageIndex, viewPager));
        }
    }

    @Override
    public void onDragEnter(DragInfo dragInfo, ViewPager viewPager) {
        //do nothing
    }

    @Override
    public void onDragExit(DragInfo dragInfo, ViewPager viewPager) {
        //do nothing
    }

    @Override
    public void onDragOver(DragInfo dragInfo, final ViewPager viewPager) {

        checkTouchMove(dragInfo, viewPager);

        shadowSizeX = dragInfo.shadowSize.x;

        final float x = dragInfo.dragX;
        final float y = dragInfo.dragY;

        final float visualCenterX = x - dragInfo.shadowTouchPoint.x + dragInfo.shadowSize.x / 2f;
        final float visualCenterY = y - dragInfo.shadowTouchPoint.y + dragInfo.shadowSize.y / 2f;
        // Check if we are hovering over the scroll areas
        mDistanceSinceScroll += Math.hypot(mLastTouch[0] - visualCenterX, mLastTouch[1] - visualCenterY);
        mLastTouch[0] = (int) visualCenterX;
        mLastTouch[1] = (int) visualCenterY;
        checkScrollState(viewPager, visualCenterX, visualCenterY, shadowSizeX);
    }

    private void checkTouchMove(final DragInfo dragInfo, ViewPager viewPager) {
        final int currentItem = viewPager.getCurrentItem();
        DragListenerDispatcher dragListener = getDragListener(currentItem, viewPager);
        View pageView = getPageView(dragInfo.pageIndex, viewPager);
        if (pageView == null) {
            throw new IllegalStateException("has error on move, pageView == null, " +
                    "dragging pageIndex = " + dragInfo.pageIndex +
                    ", pageAdapter = " + viewPager.getAdapter());
        }
        if (dragListener != null) {
            if (mLastDragLisener != dragListener) {

                final View nextView = getPageView(currentItem, viewPager);
                if (nextView == null) {
                    throw new IllegalStateException("has error on move, nextView == null, " +
                            "current pageIndex = " + currentItem +
                            ", pageAdapter = " + viewPager.getAdapter());
                }
                if (mLastDragLisener != null) {//(mLastDragLisener == null) is first drag, first drag do nothing
                    mLastDragLisener.onDragExit(dragInfo, pageView);

                    //下一页第一个 或 上一页最后一个 进行替换
                    //make new drag info
                    ((RecyclerView) pageView).getItemAnimator().endAnimations();
                    final DragInfo newDragInfo = new DragInfo();
                    int moveIndex = currentItem >= dragInfo.pageIndex ? 0 : ((RecyclerView) nextView).getAdapter().getItemCount() - 1;
                    newDragInfo.itemId = ((RecyclerView) nextView).getAdapter().getItemId(moveIndex);
                    newDragInfo.draggingView = ((RecyclerView) nextView).findViewHolderForAdapterPosition(moveIndex).itemView;
                    newDragInfo.pageIndex = currentItem;
                    mLastDragLisener.onPageTransfer(dragInfo, newDragInfo);
                    //end page transfer
                    dragInfo.pageIndex = newDragInfo.pageIndex;
                    dragInfo.draggingView = newDragInfo.draggingView;

                }

                //on enter
                dragListener.onDragEnter(dragInfo, nextView);

                pageView = nextView;
            }
            //on hover
            dragListener.onDragOver(dragInfo, pageView);
        } else {
            if (mLastDragLisener != null) {
                mLastDragLisener.onDragExit(dragInfo, pageView);
            }
        }
        mLastDragLisener = dragListener;
    }

    private void checkScrollState(final ViewPager viewPager, float x, float y, float shadowSize) {
        final int slop = ViewConfiguration.get(viewPager.getContext()).getScaledWindowTouchSlop();
        final int delay = mDistanceSinceScroll < slop ? RESCROLL_DELAY : SCROLL_DELAY;
        if ((x - shadowSize /4f) < leftOutZone) {//Turn to the left page
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                mScrollRunnable.setDirection(SCROLL_LEFT);
                mHandler.postDelayed(mScrollRunnable, delay);
            }
        } else if ((x + shadowSize /4f) > viewPager.getWidth() - rightOutZone) {
            if (mScrollState == SCROLL_OUTSIDE_ZONE) {
                mScrollState = SCROLL_WAITING_IN_ZONE;
                mScrollRunnable.setDirection(SCROLL_RIGHT);
                mHandler.postDelayed(mScrollRunnable, delay);
            }
        } else {
            clearScrollRunnable();
        }
    }

    private void clearScrollRunnable() {
        mHandler.removeCallbacks(mScrollRunnable);
        if (mScrollState == SCROLL_WAITING_IN_ZONE) {
            mScrollState = SCROLL_OUTSIDE_ZONE;
            mScrollRunnable.setDirection(SCROLL_RIGHT);
        }
    }

    private ScrollRunnable mScrollRunnable;

    private class ScrollRunnable implements Runnable {
        private int mDirection;
        private ViewPager viewPager;

        ScrollRunnable(ViewPager viewPager) {
            this.viewPager = viewPager;
        }

        public void run() {
            int currentItem = viewPager.getCurrentItem();

            if (mDirection == SCROLL_LEFT) {
                viewPager.setCurrentItem(currentItem - 1);
            } else {
                viewPager.setCurrentItem(currentItem + 1);
            }
            mScrollState = SCROLL_OUTSIDE_ZONE;
            mDistanceSinceScroll = 0;
            if (isDragging()) {
                // Check the scroll again so that we can requeue the scroller if necessary
                checkScrollState(viewPager, mLastTouch[0], mLastTouch[1], shadowSizeX);
            }
        }

        void setDirection(int direction) {
            mDirection = direction;
        }
    }

    public boolean isDragging() {
        return mDragging;
    }

    @Override
    public boolean acceptDrop(DragInfo dragInfo, ViewPager viewPager) {
        DragListenerDispatcher dragListener = getDragListener(dragInfo.pageIndex, viewPager);
        return dragListener != null && dragListener.acceptDrop(dragInfo, getPageView(dragInfo.pageIndex, viewPager));
    }

    @Override
    public long getDraggingId() {
        //do nothing
        return 0;
    }

    @Override
    public PointF getLastTouchPoint() {
        //do nothing
        return null;
    }

    @Override
    public void clearMove() {
        //do nothing
    }

    @Override
    public void onPageTransfer(DragInfo lastDragInfo, DragInfo dragInfo) {
        //do nothing
    }

    private View getPageView(int pageIndex, ViewPager viewPager) {
        if (viewPager.getAdapter() == null || !(viewPager.getAdapter() instanceof BaseDragPageAdapter)) {
            return null;
        }
        BaseDragPageAdapter dragPageAdapter = (BaseDragPageAdapter) viewPager.getAdapter();
        return dragPageAdapter.getPage(pageIndex);
    }
}
