package com.okamoba.okaevent_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // ログ用のタグ
    private String TAG = "MainActivity";

    // Firebase認証
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.eventsRecyclerView);
        EventAdapter adapter = new EventAdapter(Arrays.asList("イベント1", "イベント2", "イベント3")); // TODO:Firestore から取得する
        recyclerView.setAdapter(adapter);

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
                    ((TextView) findViewById(R.id.main_TX_name)).setText(user.getDisplayName());
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

        // set button listener
        findViewById(R.id.main_BT_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show PopupMenu
                PopupMenu popup = new PopupMenu(MainActivity.this, findViewById(R.id.main_BT_setting));
                popup.getMenuInflater().inflate(R.menu.edit_profile, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_editProfile:
                                Intent intent = new Intent(MainActivity.this, SetUserInfoActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.item_signOut:
                                signOut();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
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
