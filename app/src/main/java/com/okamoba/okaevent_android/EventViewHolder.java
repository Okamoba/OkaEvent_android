package com.okamoba.okaevent_android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by tatsuhama on 2018/02/18.
 * 各イベントを表示するための　ViewHolder
 */

class EventViewHolder extends RecyclerView.ViewHolder {

    EventViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false));
    }

    void bindEvent(String event) { // TODO:イベントモデルに差し替える
        // TODO:デザイン適用
        TextView textView = itemView.findViewById(R.id.text);
        textView.setText(event);
    }

}
