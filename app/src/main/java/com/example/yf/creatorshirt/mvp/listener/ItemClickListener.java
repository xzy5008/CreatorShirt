package com.example.yf.creatorshirt.mvp.listener;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yang on 2017/6/18.
 */

public class ItemClickListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener clickListener;

    public interface OnItemClickListener {
        /**
         * 点击时的回调
         *
         * @param view
         * @param position 点击的位置
         */
        void onItemClick(View view, int position);


    }

    public interface OnItemObjectClickListener {
        void onItemClick(Object o);
    }

    public interface OnItemComClickListener {
        void onItemClick(Object o, View view);
    }


    public interface OnClickListener {
        void onClick(View view, int position);
    }

    public ItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}
