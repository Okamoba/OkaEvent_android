package com.okamoba.okaevent_android;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by keiichilo on 2018/01/22.
 */

public class SetEmailPasswordDialog extends AppCompatDialogFragment {

    public boolean toSetEmail;    // if update user Email, true

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private View content;
    private boolean updated;

    private DialogInterface.OnDismissListener m_onDismissListener;

    public SetEmailPasswordDialog() {
        super();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        toSetEmail = true;
        updated = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(this.getContext());
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        content = inflater.inflate(R.layout.dialog_setinfo_reauth, null);
        dialog.setContentView(content);

        if (toSetEmail) {
            content.findViewById(R.id.setinfo_dialog_TX_newPassword).setVisibility(View.GONE);
            content.findViewById(R.id.setinfo_dialog_ET_newPassword).setVisibility(View.GONE);
            ((EditText)content.findViewById(R.id.setinfo_dialog_ET_email)).setText(mUser.getEmail());
        } else {
            content.findViewById(R.id.setinfo_dialog_TX_email).setVisibility(View.GONE);
            content.findViewById(R.id.setinfo_dialog_ET_email).setVisibility(View.GONE);
        }

        content.findViewById(R.id.setinfo_dialog_BT_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonEnable(false);
                if (toSetEmail) {
                    updateEmail();
                } else {
                    updatePassword();
                }
            }
        });

        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        updated = false;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        m_onDismissListener.onDismiss(getDialog());

        content = null;
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) { m_onDismissListener = listener; }

    public boolean isUpdated() { return updated; }

    private void updateEmail() {
        final String password = ((EditText)content.findViewById(R.id.setinfo_dialog_ET_Password)).getText().toString();
        final String newEmail = ((EditText)content.findViewById(R.id.setinfo_dialog_ET_email)).getText().toString();
        if (password.equals("") || newEmail.equals("")) {
            setButtonEnable(true);
            setMessageText("Please fill the blanks");
            return;
        }

        AuthCredential credential = EmailAuthProvider
                .getCredential(mUser.getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        mUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //update email
                            mUser.updateEmail(newEmail)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                updated = true;
                                                dismiss();
                                            } else {    // failed to update email
                                                setButtonEnable(true);
                                                if (task.getException() != null)
                                                    setMessageText(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {    // failed to re-authenticate
                            setButtonEnable(true);
                            if (task.getException() != null) {
                                setMessageText(task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    private void updatePassword() {
        final String password = ((EditText)content.findViewById(R.id.setinfo_dialog_ET_Password)).getText().toString();
        final String newPassword = ((EditText)content.findViewById(R.id.setinfo_dialog_ET_newPassword)).getText().toString();
        if (password.equals("") || newPassword.equals("")) {
            setButtonEnable(true);
            setMessageText("Please fill the blanks");
            return;
        }

        AuthCredential credential = EmailAuthProvider
                .getCredential(mUser.getEmail(), password);

        // Prompt the user to re-provide their sign-in credentials
        mUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // update user password
                            mUser.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                updated = true;
                                                dismiss();
                                            } else {    // failed to update user password
                                                setButtonEnable(true);
                                                if (task.getException() != null)
                                                    setMessageText(task.getException().getMessage());

                                            }
                                        }
                                    });
                        } else {    // failed to re-authenticate
                            setButtonEnable(true);
                            if (task.getException() != null) {
                                setMessageText(task.getException().getMessage());
                            }
                        }
                    }
                });
    }

    private void setButtonEnable(boolean enable) {
        if (content == null)
            return;

        Button button = content.findViewById(R.id.setinfo_dialog_BT_update);
        if (enable) {
            button.setText("Update");
            button.setEnabled(true);
        } else {
            button.setText("updating...");
            button.setEnabled(false);
        }
    }

    private void setMessageText(String text) {
        ((TextView)content.findViewById(R.id.setinfo_dialog_TX_message)).setText(text);
    }
}
