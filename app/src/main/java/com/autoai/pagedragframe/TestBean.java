package com.autoai.pagedragframe;

public class TestBean  {
    public int color;
    public int dataIndex;
    public final int id;

    public TestBean(int color, int dataIndex) {
        this.color = color;
        this.dataIndex = dataIndex;
        id = dataIndex + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBean testBean = (TestBean) o;
        return color == testBean.color && dataIndex == testBean.dataIndex && this.id == testBean.id;
    }

    @Override
    public String toString() {
        return "TestBean{" +
                "color=" + color +
                ", dataIndex=" + dataIndex +
                '}';
    }
}
