package com.bs.graduation.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 自定义继承RecyclerView.ViewHolder的viewHolder
 */
public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder{

    /**
     * RecyclerView的item点击监听
     */
    private RVItemClickListener listener;

    /**构造函数
     */
    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    /**
     * 构造函数
     */
    public RecyclerViewHolder(View itemView, RVItemClickListener listener) {
        super(itemView);
        this.listener = listener;
    }

    /**
     * 设置子View点击事件
     * @param view         要绑定的view
     * @param viewHolder   viewHolder
     */
    public void setItemOnClickListener(View view, final RecyclerViewHolder viewHolder, final Object object){
        if(view != null && listener != null){
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(viewHolder, object);
                }
            });
        }
    }


    public interface RVItemClickListener {
        void onItemClick(RecyclerViewHolder viewHolder, Object object);
    }
}
