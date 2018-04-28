package com.okamoba.okaevent_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    // ログ用のタグ
    private String TAG = "MainActivity";

    // Firebase認証
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private EventAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        RecyclerView recyclerView = findViewById(R.id.eventsRecyclerView);
        mAdapter = new EventAdapter(mFireStore.collection("events"));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            // イベントがタップされたときの処理
            @Override
            public void onItemClick(View view, int position) {
                EventModel event = mAdapter.getEvent(position);
                if (event.getAuthor() == mAuth.getUid()) {
                    // TODO: 2018/04/28 イベントを編集できるようにする 
                } else {
                    // TODO: 2018/04/28 イベントの詳細を表示する 
                }
            }
        }));

        //-------------------------------------------------------------------------------
        /*
        Firebase認証
         */
        // Firebaseの認証インスタンスの取得
        mAuth = FirebaseAuth.getInstance();
        // Firebaseの認証リスナーを作成
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // サインインできてないときは、サインインする
        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, SigninActivity.class);
            // MainActivityに戻れないようにする
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        //-------------------------------------------------------------------------------

        // イベント追加ボタンのアクション
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_BT_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent event_intent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(event_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_editProfile:
                Intent intent = new Intent(MainActivity.this, SetUserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.item_signOut:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Firebase認証リスナー追加
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && (user.getDisplayName() == null || user.getDisplayName().equals(""))) {
            Intent intent = new Intent(this, SetUserInfoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Firebase認証リスナー解除
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void signOut() {
        mAuth.signOut();

        Intent intent = new Intent(this, SigninActivity.class);
        // MainActivityに戻れないようにする
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
