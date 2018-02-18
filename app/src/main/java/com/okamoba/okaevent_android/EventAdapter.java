package com.okamoba.okaevent_android;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by tatsuhama on 2018/02/18.
 * イベント一覧を表示するためのアダプタ
 */
public class EventAdapter extends RecyclerView.Adapter {

    private List<String> mEvents;

    EventAdapter(List<String> events) {
        mEvents = events;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EventViewHolder) {
            ((EventViewHolder) holder).bindEvent(mEvents.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
