package com.okamoba.okaevent_android;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tatsuhama on 2018/02/18.
 * イベント一覧を表示するためのアダプタ
 */
public class EventAdapter extends RecyclerView.Adapter {

    private List<String> mEvents = new ArrayList<>();

    EventAdapter(CollectionReference events) {
        events.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                // TODO:Document から Event に変換
                                mEvents.add(documentSnapshot.getId());
                            }
                            notifyDataSetChanged();
                        }
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
}
