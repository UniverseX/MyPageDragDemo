package com.autoai.pagedragframe;


import java.util.Objects;

public class TestBean  {
    public int color;
    public int dataIndex;

    public TestBean(int color, int dataIndex) {
        this.color = color;
        this.dataIndex = dataIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBean testBean = (TestBean) o;
        return color == testBean.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataIndex);
    }
}
