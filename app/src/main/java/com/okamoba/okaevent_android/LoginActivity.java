package com.okamoba.okaevent_android;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by kondoutomoko on 2017/12/10.
 * 認証するアクティビティです。
 * Emailとパスワードを入力して、認証するだけです。
 */

public class LoginActivity extends Activity {

    // ログ用のタグ
    private String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebaseのインスタンスを取得します。
        mAuth = FirebaseAuth.getInstance();

        // ビューの設定
        setContentView(R.layout.activity_login);
        // ボタンのリスナー設定
        Button button = findViewById(R.id.login_BT_signin);
        button.setOnClickListener(new View.OnClickListener() {
            // Emailとパスワードを取得して、ユーザー登録
            @Override
            public void onClick(View view) {
                final String email = ((EditText)findViewById(R.id.login_ET_email)).getText().toString();
                final String password = ((EditText)findViewById(R.id.login_ET_password)).getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "認証失敗",
                                            Toast.LENGTH_SHORT).show();

                                    // 失敗のメッセージを表示する
                                    if (task.getException() != null)
                                        ((TextView)findViewById(R.id.login_TX_message)).setText(task.getException().getMessage());
                                    return;
                                }

                                // Activityを終了する
                                finish();
                            }
                        });
            }
        });
    }
}
