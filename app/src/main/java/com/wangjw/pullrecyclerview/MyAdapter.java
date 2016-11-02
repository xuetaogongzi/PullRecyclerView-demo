package com.wangjw.pullrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by wangjw on 16/11/2.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements View.OnClickListener {

    private Context mContenx;

    private int mTestCount = 0;

    public MyAdapter(Context context) {
        mContenx = context;
    }

    public void addData() {
        mTestCount += 10;
        notifyDataSetChanged();
    }

    public void resetCount() {
        mTestCount = 0;
    }

    @Override
    public int getItemCount() {
        return mTestCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContenx);
        View itemView = inflater.inflate(R.layout.list_item_my, null);
        MyViewHolder holder = new MyViewHolder(itemView);
        holder.mTvTitle.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTvTitle.setText("Item " + (position + 1));

        holder.mTvTitle.setTag(position);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Toast.makeText(mContenx, "You click potion : " + (position + 1), Toast.LENGTH_SHORT).show();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);

            mTvTitle = (TextView) itemView.findViewById(R.id.TextView_Title);
        }
    }

}
