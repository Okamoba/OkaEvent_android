package com.okamoba.okaevent_android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by keiichilo on 2018/01/21.
 */

public class SigninActivity extends Activity {

    // ログ用のタグ
    private String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private boolean finish;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Firebaseのインスタンスを取得します。
        mAuth = FirebaseAuth.getInstance();

        finish = false;

        // ボタンのリスナー設定
        // sign_in button
        findViewById(R.id.signin_BT_signin).setOnClickListener(new View.OnClickListener() {
            // Emailとパスワードを取得して、ユーザー登録
            @Override
            public void onClick(View view) {
                String email = ((EditText)findViewById(R.id.signin_ET_email)).getText().toString();
                String password = ((EditText)findViewById(R.id.signin_ET_password)).getText().toString();

                changeButtons(true, false);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SigninActivity.this, "認証失敗",
                                            Toast.LENGTH_SHORT).show();

                                    changeButtons(false, false);

                                    // 失敗のメッセージを表示する
                                    if (task.getException() != null)
                                        ((TextView)findViewById(R.id.signin_TX_message)).setText(task.getException().getMessage());
                                    return;
                                }

                                // Activityを終了する
                                finish();
                            }
                        });
            }
        });
        // login button
        findViewById(R.id.signin_BT_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)findViewById(R.id.signin_ET_email)).getText().toString();
                String password = ((EditText)findViewById(R.id.signin_ET_password)).getText().toString();

                changeButtons(false, true);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SigninActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SigninActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                    changeButtons(false, false);

                                    // 失敗のメッセージを表示する
                                    if (task.getException() != null)
                                        ((TextView)findViewById(R.id.signin_TX_message)).setText(task.getException().getMessage());
                                }

                                // ...
                            }
                        });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish = true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();

        if (!finish) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void changeButtons(boolean isSignin, boolean isLogin) {
        Button btn_signin = findViewById(R.id.signin_BT_signin);
        Button btn_login = findViewById(R.id.signin_BT_login);
        if (isSignin) {
            btn_signin.setText("signing in...");
            btn_signin.setEnabled(false);
            btn_login.setText("Login");
            btn_login.setEnabled(false);
        } else if (isLogin) {
            btn_signin.setText("Sign in");
            btn_signin.setEnabled(false);
            btn_login.setText("login...");
            btn_login.setEnabled(false);
        } else {
            btn_signin.setText("Sign in");
            btn_signin.setEnabled(true);
            btn_login.setText("Login");
            btn_login.setEnabled(true);
        }
    }
}
