package com.okamoba.okaevent_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by keiichilo on 2018/01/21.
 */

public class SetUserInfoActivity extends AppCompatActivity {

    private String TAG = "SetUserInfoActivity";    // tag for debug log

    private FirebaseAuth mAuth;
    private EditText et_name;
    private String name;
    private SetEmailPasswordDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setuserinfo);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new SetEmailPasswordDialog();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mDialog.isUpdated()) {
//                    finish();
                }
            }
        });

        //-------------------------------------------------------------------------------
        /*
        init UI
         */
        // init EditTexts
        FirebaseUser user = mAuth.getCurrentUser();
        name = (user != null) ? user.getDisplayName() : "";
        et_name = findViewById(R.id.userinfo_ET_name);
        et_name.setText(name);

        if (name == null || name.equals("")) {
            findViewById(R.id.userinfo_BT_changeEmail).setVisibility(View.INVISIBLE);
            findViewById(R.id.userinfo_BT_changePassword).setVisibility(View.INVISIBLE);
            ((Button)findViewById(R.id.userinfo_BT_update)).setText("Register");
        }

        // init Buttons
        final Button btn_update = findViewById(R.id.userinfo_BT_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_name.getText().toString().equals("")
                        && !et_name.getText().toString().equals(name)) {
                    btn_update.setText("updating...");
                    btn_update.setEnabled(false);
                    setDisplayName(et_name.getText().toString());
                } else {
                    ((TextView) findViewById(R.id.userinfo_TX_message)).setText("Please set name");
                }
            }
        });
        findViewById(R.id.userinfo_BT_changeEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.toSetEmail = true;
                mDialog.show(getSupportFragmentManager(),"");
            }
        });
        findViewById(R.id.userinfo_BT_changePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.toSetEmail = false;
                mDialog.show(getSupportFragmentManager(),"");
            }
        });
        //-------------------------------------------------------------------------------
    }

    @Override
    public void finish() {
        super.finish();

        if (name == null || name.equals("")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void setDisplayName(String new_name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(new_name)
                .build();

        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "User profile updated.");
                        }
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        ((TextView) findViewById(R.id.userinfo_TX_message)).setText(e.getMessage());
                        findViewById(R.id.userinfo_BT_update).setEnabled(true);
                        ((Button)findViewById(R.id.userinfo_BT_update)).setText("Update");
                    }
                });
    }
}
