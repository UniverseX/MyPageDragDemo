package com.autoai.pagedragframe.drag;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.autoai.pagedragframe.drag.dragimp.RecyclerDragListenerImp;
import com.autoai.pagedragframe.viewpager.BaseGridPagerAdapter;

import java.util.List;

public abstract class BaseDragPageAdapter<V> extends BaseGridPagerAdapter<V> implements View.OnLongClickListener {
    private View.OnLongClickListener onLongClickListener;
    private DragManager<RecyclerView> mDragManager;

    public BaseDragPageAdapter(Context context, int row, int column, List<V> list, DragListenerDispatcher<ViewPager, DragInfo> pagerLisener) {
        super(context, row, column, list);

        mDragManager = new MyDragManager();
        pagerLisener.attachDragManager(mDragManager);

    }

    @Override
    public boolean onLongClick(View v) {
        if (onLongClickListener != null) {
            if (onLongClickListener.onLongClick(v)) {
                return true;
            }
        }
        RecyclerView recyclerView = (RecyclerView) v.getParent();
        if (recyclerView != null) {
            int childIndex = recyclerView.getChildAdapterPosition(v);
            PageViewHolder childViewHolder = (PageViewHolder) recyclerView.getChildViewHolder(v);
            DragInfo dragInfo = new DragInfo();
            dragInfo.pageIndex = childViewHolder.pageIndex;
            dragInfo.itemId = childViewHolder.getItemId();
            mDragManager.startDrag(v, dragInfo);
            recyclerView.getAdapter().notifyItemChanged(childIndex);
        }
        return true;
    }

    public View.DragShadowBuilder getDragShadowBuilder(View view, Point touchPoint){
        return new NoForegroundShadowBuilder(view, touchPoint);
    }

    @Override
    public void onBindPage(Context context, RecyclerView recyclerView, int pageIndex) {
        super.onBindPage(context, recyclerView, pageIndex);
        recyclerView.setTag(pageIndex);
        mDragManager.addDragListener(pageIndex, new RecyclerDragListenerImp(recyclerView, (DragNotifier) recyclerView.getAdapter()));
    }

    @Override
    public void onUnbindPage(RecyclerView view, int pageIndex) {
        super.onUnbindPage(view, pageIndex);
        mDragManager.removeDragListener(pageIndex);
    }

    @Override
    public void release() {
        super.release();
        mDragManager.clearListeners();
    }

    class MyDragManager extends DragManager<RecyclerView> {

        @Override
        public View.DragShadowBuilder getViewShadowBuilder(View view, Point touchPoint) {
            return getDragShadowBuilder(view, touchPoint);
        }
    }

    public abstract class DragAdapter<VH extends PageViewHolder> extends GridRecycleAdapter<VH> implements DragNotifier {
        public DragAdapter(List<V> list, int pageIndex) {
            super(list, pageIndex);
            setHasStableIds(true);
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = super.onCreateViewHolder(parent, viewType);
            vh.itemView.setOnLongClickListener(BaseDragPageAdapter.this);
            return vh;
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            super.onBindViewHolder(holder, position);
            long draggingId = getDraggingId(mPageIndex);
            holder.itemView.setVisibility(draggingId == getItemId(position) ? View.INVISIBLE : View.VISIBLE);
            holder.itemView.setAlpha(draggingId == getItemId(position) ? 0f : 1f);
            holder.itemView.postInvalidate();
        }

        @Override
        public void onDragStart(int position, View draggingView) {
            notifyItemChanged(position);
        }

        @Override
        public void onDragEnd(int position, View draggingView) {
            notifyItemChanged(position);
        }

        @Override
        public void onDragEnter(int position, View newView) {
            notifyItemChanged(position);
        }

        @Override
        public void onDragExit(int position, View lastView) {
            notifyItemChanged(position);
        }

        @Override
        public void onDrop(long itemId, View draggingView) {
        }

        @Override
        public void onMove(int fromPosition, int toPosition) {
            if(fromPosition == RecyclerView.NO_POSITION || toPosition == RecyclerView.NO_POSITION){
                return;
            }
            if(fromPosition == toPosition){
                return;
            }
            //按顺序交换，不直接交换,do not use Collections.swap, ensure the order
            List<V> data = getData();
            V removeItem = data.remove(fromPosition);
            data.add(toPosition, removeItem);

            updateData(data);
        }

        @Override
        public void onPageTransfer(DragInfo lastInfo, DragInfo newInfo) {
            if(lastInfo.itemId == -1 || newInfo.itemId == -1){
                return;
            }
            int draggingPosition = getPageChildIndexById(lastInfo.pageIndex, lastInfo.itemId);
            int newPagePosition = getPageChildIndexById(newInfo.pageIndex, newInfo.itemId);
            //do not use Collections.swap, ensure the order
            switchPageItem(draggingPosition, newPagePosition);

            notifyPageChanged(lastInfo.pageIndex);
            notifyPageChanged(newInfo.pageIndex);
        }

        public int getPageChildIndexById(int pageIndex, long itemId){
            RecyclerView recyclerView = getPage(pageIndex);
            if(recyclerView.getAdapter() != null){
                DragAdapter adapter = (DragAdapter) recyclerView.getAdapter();
                return transToDataListIndex(pageIndex, adapter.getPositionForId(itemId));
            }
            return RecyclerView.NO_POSITION;
        }
    }

    public void setItemOnLongClickListener(View.OnLongClickListener listener) {
        onLongClickListener = listener;
    }

    public long getDraggingId(int pageIndex){
        return mDragManager.getDraggingId(pageIndex);
    }

    public abstract DragAdapter generateItemAdapter(List<V> data, int pageIndex);
}
