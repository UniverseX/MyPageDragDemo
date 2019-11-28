package com.autoai.pagedragframe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.autoai.pagedrag.bean.DataComparator;
import com.autoai.pagedrag.bean.PageData;
import com.autoai.pagedrag.views.DragViewPager;
import com.autoai.pagedragframe.drag.dragimp.ViewPagerDragListenerImp;
import com.autoai.pagedragframe.test.drag.MyAdapter;
import com.autoai.pagedragframe.viewpager.RecycleViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//public class MainActivity extends AppCompatActivity {
//
//    private ArrayList<TestBean> data;
//    private RecycleViewPager vp;
//    int[] color_array = {Color.DKGRAY, Color.YELLOW, Color.BLUE,
//            Color.CYAN, Color.GRAY, Color.RED, Color.GREEN, Color.MAGENTA, Color.WHITE, Color.LTGRAY};
//    Random r = new Random();
//    private ViewPagerDragListenerImp dragListener;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initData();
//
//        vp = (RecycleViewPager) this.findViewById(R.id.vp);
//
//        dragListener = new ViewPagerDragListenerImp(vp);
//        //边界宽度定义
//        dragListener.setLeftOutZone(100);
//        dragListener.setRightOutZone(100);
//        GridPagerAdapter adapter = new GridPagerAdapter(this, data, dragListener);
//        vp.setAdapter(adapter);
//        vp.setOnDragListener(dragListener);
//    }
//
//    private void initData() {
//        final ArrayList<TestBean> colors = new ArrayList<>();
//        for (int i = 0; i < 24; i++) {
//            colors.add(new TestBean(color_array[r.nextInt(color_array.length)], i));
//        }
//        data = new ArrayList<>(colors);
//
//    }
//
//    public void insert(View view) {
//    }
//
//    public void remove(View view) {
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        dragListener.release();
//    }
//}

public class MainActivity extends AppCompatActivity {

    private DragViewPager vp;
    int[] color_array = {Color.DKGRAY, Color.YELLOW, Color.BLUE,
            Color.CYAN, Color.GRAY, Color.RED, Color.GREEN, Color.MAGENTA, Color.WHITE, Color.LTGRAY};
    Random r = new Random();
    private ViewPagerDragListenerImp dragListener;
    private ArrayList<TestBean> colors;
    private PageData<TestBean> pageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

        vp = (DragViewPager) this.findViewById(R.id.vp);

        MyAdapter myAdapter = new MyAdapter(this, pageData);
        //边界宽度定义
        vp.setLeftOutZone(100);
        vp.setRightOutZone(100);
        vp.setViewPagerHelper(myAdapter);
        vp.setAdapter(myAdapter);
    }

    private void initData() {
        colors = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            colors.add(new TestBean(color_array[r.nextInt(color_array.length)], i));
        }
        pageData = new PageData<>(2, 5, colors, new DataComparator<TestBean>() {
            @Override
            public boolean areItemsTheSame(TestBean oldData, TestBean newData) {
                return oldData.hashCode() == newData.hashCode();
            }

            @Override
            public boolean areContentsTheSame(TestBean oldData, TestBean newData) {
                return oldData.color == newData.color;
            }

            @Override
            public Object getChangePayload(TestBean oldData, TestBean newData) {
                return newData.color;
            }

            @Override
            public int getDataPosition(List<TestBean> allData, TestBean newData) {
                Log.d("zxl_test", "MainActivity -- getDataPosition: newData = " + newData);
                Log.d("zxl_test", "MainActivity -- getDataPosition: pos1Data = " + allData.get(1));
                return allData.indexOf(newData);
            }
        });
    }

    public void insert(View view) {
        final int pos = colors.size();
        colors.add(0, new TestBean(color_array[r.nextInt(color_array.length)], pos));
        pageData.insertData(0, colors.get(pos));
    }

    public void remove(View view) {
        TestBean remove = colors.remove(r.nextInt(colors.size()));
        pageData.removeData(remove);
    }

    public void update(View view) {
        TestBean testBean = colors.get(1);

        int newColor = color_array[r.nextInt(color_array.length)];
        testBean.color = newColor;
        pageData.updateData(testBean, newColor);

//        int dataIndex = 100 + r.nextInt(100);
//        testBean.dataIndex = dataIndex;
//        pageData.updateData(testBean, dataIndex);
    }
}
