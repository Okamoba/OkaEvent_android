package com.okamoba.okaevent_android;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by keiichilo on 2018/02/20.
 */

public class SendPostEventApiTask extends AsyncTask<Map<String, Object>, Integer, Void> {
    @SuppressLint("StaticFieldLeak")
    private TextView tV_error;
    private ProgressDialog dialog;

    private String errorMessage;

    public SendPostEventApiTask(Context context, TextView textView_error) {
        tV_error = textView_error;

        dialog = new ProgressDialog(context);
        dialog.setTitle("イベント新規作成");
        dialog.setMessage("作成中");
        dialog.setCancelable(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    @Override
    protected Void doInBackground(Map<String, Object>[] events) {
        // Eventマップの内容をjsonにエンコードする
        JSONObject[] jsonObjects = new JSONObject[events.length];
        int i = 0;
        for (Map<String, Object> event : events) {
            jsonObjects[i] = new JSONObject();
            for (Map.Entry<String, Object> data : event.entrySet()) {
                try {
                    jsonObjects[i].put(data.getKey(), data.getValue());
                } catch (JSONException e) {
                    errorMessage = "failed to encode json";
                    e.printStackTrace();
                    return null;
                }
            }
            i++;
        }

        i = 0;
        for (i = 0; i < jsonObjects.length; i++) {
            try {
                URL url = new URL("https://us-central1-okaevent-7bd1d.cloudfunctions.net/postEvent");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                // create the SSL connection
                SSLContext sc;
                sc = SSLContext.getInstance("TLS");
                sc.init(null, null, new java.security.SecureRandom());
                connection.setSSLSocketFactory(sc.getSocketFactory());

                // データ送信
                OutputStream os = connection.getOutputStream();
                os.write(jsonObjects[i].toString().getBytes("UTF-8"));
                os.close();

                // 応答待ち
                if (connection.getResponseCode() != 200) {    // 失敗時
                    errorMessage = connection.getResponseMessage();
                    connection.disconnect();
                    return null;
                } else {    // 成功時
                    publishProgress(i);
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "イベントの作成に失敗しました。";
                return null;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (errorMessage != null) {
            tV_error.setText(errorMessage);
            tV_error.setVisibility(View.VISIBLE);
        }
        if (dialog.isShowing())
            dialog.dismiss();
        if (finishListener != null)
            finishListener.onFinish(errorMessage == null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        errorMessage = null;
        if (!dialog.isShowing())
            dialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (successListener != null)
            successListener.onSuccess(values[0]);
    }

    private OnSuccessListener successListener;
    public void setSuccessListener(OnSuccessListener listener) {
        this.successListener = listener;
    }
    private OnFinishListener finishListener;
    public void setFinishListener(OnFinishListener listener) {
        this.finishListener = listener;
    }

    interface OnSuccessListener {
        public void onSuccess(int index);
    }
    interface OnFinishListener {
        public void onFinish(boolean isSuccess);
    }
}
