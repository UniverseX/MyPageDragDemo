package com.autoai.pagedragframe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.autoai.pagedragframe.drag.dragimp.ViewPagerDragListenerImp;
import com.autoai.pagedragframe.viewpager.MViewPager;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TestBean> data;
    private MViewPager vp;
    int[] color_array = {Color.DKGRAY, Color.YELLOW, Color.BLUE,
            Color.CYAN, Color.GRAY, Color.RED, Color.GREEN, Color.MAGENTA, Color.WHITE, Color.LTGRAY};
    Random r = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

        vp = (MViewPager) this.findViewById(R.id.vp);

        ViewPagerDragListenerImp dragListener = new ViewPagerDragListenerImp(vp);
        GridPagerAdapter adapter = new GridPagerAdapter(this, data, dragListener);
        vp.setAdapter(adapter);
        vp.setOnDragListener(dragListener);
    }

    private void initData() {
        final ArrayList<TestBean> colors = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            colors.add(new TestBean(color_array[r.nextInt(color_array.length)], i));
        }
        data = new ArrayList<>(colors);

    }

    public void insert(View view) {
    }

    public void remove(View view) {
    }


}
