package com.autoai.pagedragframe;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autoai.pagedragframe.drag.BaseDragPageAdapter;
import com.autoai.pagedragframe.drag.DragInfo;
import com.autoai.pagedragframe.drag.DragListenerDispatcher;

import java.util.List;

public class GridPagerAdapter extends BaseDragPageAdapter<TestBean> {

    public GridPagerAdapter(Context context, List<TestBean> list, DragListenerDispatcher<ViewPager, DragInfo> pagerListener) {
        super(context, 2, 5, list, pagerListener);
    }

    @Override
    public BaseDragPageAdapter.DragAdapter generateItemAdapter(List<TestBean> data, int pageIndex) {
        return new ItemAdapter(data, pageIndex);
    }

    class ItemAdapter extends DragAdapter<PageViewHolder> {

        public ItemAdapter(List<TestBean> list, int pageIndex) {
            super(list, pageIndex);
        }

        @Override
        public int getPositionForId(long id) {
            for (int i = 0; i < getData().size(); i++) {
                if (getData().get(i).hashCode() == id) {
                    return i;
                }
            }
            return RecyclerView.NO_POSITION;
        }

        @Override
        public long getItemId(int position) {
            return getData().get(position).hashCode();
        }

        @Override
        public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType, int pageIndex) {
            // TODO: 19-7-17  无父布局的View 不显示item 图像
            return new PageViewHolder(View.inflate(parent.getContext(), R.layout.page_item, null), pageIndex);
        }

        @Override
        public void onBindViewHolder(PageViewHolder holder, int position, int pageIndex) {
            holder.itemView.findViewById(R.id.recycler_item).setBackgroundColor(
                    Integer.valueOf(getData().get(position).color));
            ((TextView)holder.itemView.findViewById(R.id.tv_test)).setText("" + getData().get(position).dataIndex);
        }
    }

}
