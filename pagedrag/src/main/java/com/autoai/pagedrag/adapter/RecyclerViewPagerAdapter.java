package com.autoai.pagedrag.adapter;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.autoai.pagedrag.IRelease;
import com.autoai.pagedrag.bean.DataComparator;
import com.autoai.pagedrag.bean.ListDiffCallback;
import com.autoai.pagedrag.bean.PageData;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewPagerAdapter<Data> extends ViewPagerAdapter implements IRelease, PageData.DataObserver<Data> {
    private static final String TAG = "RecyclerViewPagerAdapte";

    protected final List<FrameLayout> views = new ArrayList<>();//RecyclerView's container
    protected PageData<Data> mPageData;
    private Context mContext;

    public RecyclerViewPagerAdapter(Context context, PageData<Data> pageData, boolean initViewsImmediately) {
        mContext = context;
        mPageData = pageData;
        mPageData.setDataObserver(this);

        if(initViewsImmediately) initViewFromPageData(context);
    }

    protected void initViewFromPageData(Context context) {
        int pageNum = mPageData.getPageNum();

        for (int i = 0; i < pageNum; i++) {
            FrameLayout frameLayout = new FrameLayout(context);
            RecyclerView recyclerView = onCreatePage(frameLayout, i);
            frameLayout.addView(recyclerView, generatePageLayoutParams());
            views.add(frameLayout);
        }

        init(views);
    }

    @NonNull
    public RecyclerView onCreatePage(FrameLayout frameLayout, int pageIndex) {
        final Context context = frameLayout.getContext();
        RecyclerView recyclerView = new RecyclerView(context);
        RecyclerView.Adapter adapter = generateItemRecyclerAdapter(mPageData.getPageData(pageIndex), pageIndex);
        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        } else {
            Log.w(TAG, "onCreatePage: your recycle adapter is null");
        }
        RecyclerView.LayoutManager layoutManager = generateItemLayoutManager(context, mPageData, pageIndex);
        if (layoutManager != null) {
            recyclerView.setLayoutManager(layoutManager);
        } else {
            Log.w(TAG, "onCreatePage: your LayoutManager is null");
        }
        return recyclerView;
    }

    protected FrameLayout.LayoutParams generatePageLayoutParams() {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    protected RecyclerView.LayoutManager generateItemLayoutManager(Context context, PageData<Data> pageData, int pageIndex) {
        return new GridLayoutManager(context, pageData.getColumn(), GridLayoutManager.VERTICAL, false);
    }

    protected abstract RecyclerView.Adapter generateItemRecyclerAdapter(List<Data> pageData, int pageIndex);

    @CallSuper
    @Override
    public void release() {
        mPageData.setDataObserver(null);
        views.clear();
        notifyDataSetChanged();
    }

    @Override
    public void notifyPageAdd(int pageIndex) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        RecyclerView recyclerView = onCreatePage(frameLayout, pageIndex);
        frameLayout.addView(recyclerView, generatePageLayoutParams());
        views.add(frameLayout);
        notifyDataSetChanged();
    }

    @Override
    public void notifyPageRemoved(int removePageNum) {
        for (int i = 0; i < removePageNum; i++) {
            views.remove(views.size() - 1);
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int pageIndex, int itemPosition, Object payload) {
        notifyItemRangeChanged(pageIndex, itemPosition, 1, payload);
    }

    /**
     * @param payload payload相关连的bean的修改项，不影响itemId，局部刷新才会其作用
     * @see RecyclerView#getChangedHolderKey(RecyclerView.ViewHolder)
     */
    public void notifyItemRangeChanged(int pageIndex, int startPosition, int count, Object payload) {
        RecyclerView recycleView = (RecyclerView) views.get(pageIndex).getChildAt(0);
        RecyclerView.Adapter adapter = recycleView.getAdapter();
        if (adapter != null) {
            adapter.notifyItemRangeChanged(startPosition, count, payload);
        }
    }

    @Override
    public void notifyPageChanged(int pageIndex, List<Data> newList, DataComparator<Data> dataComparator) {
        RecyclerView recycleView = (RecyclerView) views.get(pageIndex).getChildAt(0);
        RecyclerView.Adapter adapter = recycleView.getAdapter();
        if (adapter instanceof DragPageAdapter.ItemDragAdapter) {
            ((DragPageAdapter.ItemDragAdapter) adapter).updateData(newList, dataComparator);
        }
    }

    public abstract class ItemPageAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        protected List<Data> data;
        protected int pageIndex;

        public ItemPageAdapter(List<Data> list, int pageIndex) {
            this.data = list;
            this.pageIndex = pageIndex;
        }

        public void updateData(List<Data> newList, DataComparator<Data> dataComparator) {
            ListDiffCallback<Data> dataListDiffCallback = new ListDiffCallback<>(this.data, newList, dataComparator);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(dataListDiffCallback);
            this.data.clear();
            this.data.addAll(newList);
            diffResult.dispatchUpdatesTo(this);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public final void onBindViewHolder(VH holder, int position, List<Object> payloads) {
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads);
            } else {
                onBindItemViewHolder(holder, position, payloads);
            }
        }

        public void onBindItemViewHolder(VH holder, int position, List<Object> payloads) {
        }
    }
}