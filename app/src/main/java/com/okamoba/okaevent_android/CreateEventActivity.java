package com.okamoba.okaevent_android;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by keiichilo on 2018/02/18.
 */

public class CreateEventActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private ProgressDialog progressDialog;

    private EventModel event;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createevent);

        initializeDialogs();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "認証に失敗しました。", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            uid = user.getUid();
        }

        Bundle bundle = getIntent().getExtras();
        byte[] value;
        if (bundle != null && (value = getIntent().getExtras().getByteArray("event")) != null && value.length > 1) {
            event = new EventModel(value);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);

            ((EditText)findViewById(R.id.new_event_name)).setText(event.getName());
            ((EditText)findViewById(R.id.new_event_text)).setText(event.getText());
            ((EditText)findViewById(R.id.new_event_address)).setText(event.getAddress());
            ((EditText)findViewById(R.id.new_event_start_datetime)).setText(sdf.format(event.getStart_datetime()));
            ((EditText)findViewById(R.id.new_event_end_datetime)).setText(sdf.format(event.getEnd_datetime()));
            ((EditText)findViewById(R.id.new_event_url)).setText(event.getUrl());

            if (!event.getAuthor().equals(uid)) {
                // 自分が作ったイベントじゃないとき
                findViewById(R.id.new_event_name).setEnabled(false);
                ((EditText) findViewById(R.id.new_event_name)).setTextColor(Color.BLACK);
                findViewById(R.id.new_event_text).setEnabled(false);
                ((EditText) findViewById(R.id.new_event_text)).setTextColor(Color.BLACK);
                findViewById(R.id.new_event_address).setEnabled(false);
                ((EditText) findViewById(R.id.new_event_address)).setTextColor(Color.BLACK);
                findViewById(R.id.new_event_start_datetime).setEnabled(false);
                ((EditText) findViewById(R.id.new_event_start_datetime)).setTextColor(Color.BLACK);
                findViewById(R.id.new_event_end_datetime).setEnabled(false);
                ((EditText) findViewById(R.id.new_event_end_datetime)).setTextColor(Color.BLACK);
                findViewById(R.id.new_event_url).setEnabled(false);
                ((EditText) findViewById(R.id.new_event_url)).setTextColor(Color.BLACK);

                ((Button)findViewById(R.id.new_event_button)).setText("戻る");

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            } else {
                // 自分が作ったイベントのとき
                ((Button)findViewById(R.id.new_event_button)).setText("編集");

                progressDialog.setTitle("イベント編集");
                progressDialog.setMessage("編集中");

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        } else {
            event = new EventModel();
        }

        EditText eT_start_datetime = findViewById(R.id.new_event_start_datetime);
        eT_start_datetime.setTextIsSelectable(true);
        eT_start_datetime.setOnTouchListener(this);
        EditText eT_end_datetime = (EditText)findViewById(R.id.new_event_end_datetime);
        eT_end_datetime.setTextIsSelectable(true);
        eT_end_datetime.setOnTouchListener(this);

        findViewById(R.id.new_event_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.e("CHECK", "author : " + event.getDocument_id());
        if (!event.getAuthor().equals("") && !event.getAuthor().equals(uid)) {
            finish();
            return;
        }

        findViewById(R.id.new_event_error).setVisibility(View.GONE);

        String name = ((EditText)findViewById(R.id.new_event_name)).getText().toString();
        String text = ((EditText)findViewById(R.id.new_event_text)).getText().toString();
        String address = ((EditText)findViewById(R.id.new_event_address)).getText().toString();
        String start_datetime_string = ((EditText)findViewById(R.id.new_event_start_datetime)).getText().toString();
        String end_datetime_string = ((EditText)findViewById(R.id.new_event_end_datetime)).getText().toString();
        String url = ((EditText)findViewById(R.id.new_event_url)).getText().toString();
        Date start_datetime, end_datetime;

        if (name.equals("") || text.equals("") || address.equals("") || start_datetime_string.equals("") || end_datetime_string.equals("")) {
            displayErrorMessage("イベント名、説明文、会場、開始・終了時間は必須項目です。");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.JAPAN);
        try {
            start_datetime = sdf.parse(start_datetime_string);
            end_datetime = sdf.parse(end_datetime_string);
        } catch (ParseException e) {
            e.printStackTrace();
            displayErrorMessage("日時のフォーマットが変です。");
            return;
        }
        if (start_datetime.after(end_datetime)) {
            displayErrorMessage("開始日時は終了日時より前にしてください。");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            displayErrorMessage("認証に失敗しました。");
            return;
        }

        event.setAuthor(user.getUid());
        event.setName(name);
        event.setAddress(address);
        event.setText(text);
        event.setStart_datetime(start_datetime);
        event.setEnd_datetime(end_datetime);
        event.setUrl(url);

        /*****************************************************************************/
        // Cloud Firestore用のコード
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (!event.getDocument_id().equals("")) {
            db.collection("events").document(event.getDocument_id()).set(event.getEvent())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            displayErrorMessage("イベントの編集に失敗しました。");
                        }
                    });
        } else {
            db.collection("events").add(event.getEvent())
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressDialog.dismiss();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            displayErrorMessage("イベントの作成に失敗しました。");
                        }
                    });
            /*****************************************************************************/


            /*****************************************************************************/
            // CloudFunctions API用のコード
            //        final SendPostEventApiTask task = new SendPostEventApiTask(this, (TextView)findViewById(R.id.new_event_error));
            //        task.setFinishListener(new SendPostEventApiTask.OnFinishListener() {
            //            @Override
            //            public void onFinish(boolean isSuccess) {
            //                if (isSuccess) {
            //                    finish();
            //                }
            //            }
            //        });
            //        Map<String, Object>[] events = new Map[1];
            //        events[0] = event.getEvent();
            //        task.execute(events);
            /*****************************************************************************/

        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        datePickerDialog.show();
        return false;
    }

    private void initializeDialogs() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        datePickerDialog = new DatePickerDialog(CreateEventActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                int id = (getCurrentFocus() != null) ? getCurrentFocus().getId() : 0;
                if (id == R.id.new_event_start_datetime || id == R.id.new_event_end_datetime) {
                    ((EditText)getCurrentFocus()).setText(String.format(Locale.ENGLISH, "%d-%02d-%02d", year, month+1, day));
                    timePickerDialog.show();
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(CreateEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                int id = (getCurrentFocus() != null) ? getCurrentFocus().getId() : 0;
                if (id == R.id.new_event_start_datetime || id == R.id.new_event_end_datetime) {
                    String date = ((EditText)getCurrentFocus()).getText().toString();
                    ((EditText)getCurrentFocus()).setText(String.format(Locale.ENGLISH, "%s %02d:%02d:00", date, hour, minute));
                    timePickerDialog.show();
                }
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("イベント新規作成");
        progressDialog.setMessage("作成中");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void displayErrorMessage(String message) {
        TextView tV_error = findViewById(R.id.new_event_error);
        tV_error.setText(message);
        tV_error.setVisibility(View.VISIBLE);
        findViewById(R.id.new_event_scroll).setScrollY(0);
    }
}
