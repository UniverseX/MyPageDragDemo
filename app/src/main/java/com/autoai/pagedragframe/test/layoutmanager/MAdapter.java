package com.autoai.pagedragframe.test.layoutmanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.autoai.pagedragframe.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class MAdapter extends RecyclerView.Adapter<MAdapter.VM> {
    private List<UserData> userData;
    public MAdapter(List<UserData> userData){
        this.userData = userData;
    }

    @Override
    public VM onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, parent, false);
        return new VM(inflate);
    }

    @Override
    public void onBindViewHolder(VM holder, int position) {
        UserData userData = this.userData.get(position);
        Glide.with(holder.img).load(userData.imgUrl).into(holder.img);
        holder.tvName.setText(userData.name);
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public static class VM extends RecyclerView.ViewHolder{
        public ImageView img;
        public TextView tvName;
        public VM(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.iv_img);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
