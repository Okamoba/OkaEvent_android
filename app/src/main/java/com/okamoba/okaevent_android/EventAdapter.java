package com.okamoba.okaevent_android;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tatsuhama on 2018/02/18.
 * イベント一覧を表示するためのアダプタ
 */
public class EventAdapter extends RecyclerView.Adapter {

    private List<EventModel> mEvents = new ArrayList<>();

    EventAdapter(CollectionReference events) {
        events.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                mEvents.clear();
                if (documentSnapshots == null)
                    return;
                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    mEvents.add(EventModel.createFromDocumentSnapshot(documentSnapshot));
                }
                notifyDataSetChanged();
            }
        });
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

    public EventModel getEvent(int index) {
        return mEvents.get(index);
    }
}
