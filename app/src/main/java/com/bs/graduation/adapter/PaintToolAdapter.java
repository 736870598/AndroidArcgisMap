package com.bs.graduation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bs.graduation.R;

/**
 * 显示绘制图形时上面的工选项的adapter
 */
public class PaintToolAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private String[] opts = {"绘制线条","绘制矩形","绘制点","绘制圆形","多边形","清除","保存", "取消"};

    private RecyclerViewHolder.RVItemClickListener listener;

    public PaintToolAdapter(RecyclerViewHolder.RVItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return opts.length;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(new TextView(parent.getContext()),listener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.textView.setText(opts[position]);
    }

    public class MyViewHolder extends RecyclerViewHolder{

        private TextView textView;

        public MyViewHolder(View itemView, RVItemClickListener listener) {
            super(itemView, listener);
            setItemOnClickListener(itemView, this, "");
            textView = (TextView) itemView;
            textView.setBackgroundResource(R.drawable.text_bg);
            textView.setGravity(Gravity.CENTER);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);  // , 1是可选写的
            lp.setMargins(5, 20, 5, 20);
            textView.setLayoutParams(lp);
        }
    }

}
