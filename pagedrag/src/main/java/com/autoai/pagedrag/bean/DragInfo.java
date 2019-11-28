package com.autoai.pagedrag.bean;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import static android.support.v7.widget.RecyclerView.NO_ID;

public class DragInfo implements Parcelable {
    public long itemId = NO_ID;
    public Point shadowSize = new Point();
    public Point shadowTouchPoint = new Point();

    public DragInfo() {
    }

    protected DragInfo(Parcel in) {
        itemId = in.readLong();
        shadowSize = in.readParcelable(Point.class.getClassLoader());
        shadowTouchPoint = in.readParcelable(Point.class.getClassLoader());
    }

    public static final Creator<DragInfo> CREATOR = new Creator<DragInfo>() {
        @Override
        public DragInfo createFromParcel(Parcel in) {
            return new DragInfo(in);
        }

        @Override
        public DragInfo[] newArray(int size) {
            return new DragInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(itemId);
        dest.writeParcelable(shadowSize, flags);
        dest.writeParcelable(shadowTouchPoint, flags);
    }
}
