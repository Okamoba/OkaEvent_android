package com.okamoba.okaevent_android;

import android.support.annotation.NonNull;
import android.util.JsonWriter;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.security.spec.ECField;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by keiichilo on 2018/02/18.
 */

public class EventModel {

    @NonNull
    private Map<String, Object> event_map = new HashMap<String, Object>();

    @NonNull
    public String getName() {
        return checkMapValueNull("name");
    }

    public void setName(@NonNull String name) {
        event_map.put("name", name);
    }

    @NonNull
    public String getText() {
        return checkMapValueNull("text");
    }

    public void setText(@NonNull String text) {
        event_map.put("text", text);
    }

    @NonNull
    public String getAddress() {
        return checkMapValueNull("address");
    }

    public void setAddress(@NonNull String address) {
        event_map.put("address", address);
    }

    @NonNull
    public Date getStart_datetime() {
        return checkMapDateNull("start_datetime");
    }

    public void setStart_datetime(@NonNull Date start_datetime) {
        event_map.put("start_datetime", start_datetime);
    }

    @NonNull
    public Date getEnd_datetime() {
        return checkMapDateNull("end_datetime");
    }

    public void setEnd_datetime(@NonNull Date end_datetime) {
        event_map.put("end_datetime", end_datetime);
    }

    @NonNull
    public String getUrl() {
        return checkMapValueNull("url");
    }

    public void setUrl(@NonNull String url) {
        event_map.put("url", url);
    }

    @NonNull
    public String getAuthor() {
        return checkMapValueNull("author");
    }

    public void setAuthor(@NonNull String uid) {
        event_map.put("author", uid);
    }

    @NonNull
    public String getDocument_id() {
        return checkMapValueNull("document_id");
    }

    public void setDocument_id(@NonNull String document_id) {
        event_map.put("document_id", document_id);
    }

    public Map<String, Object> getEvent() {
        return event_map;
    }

    EventModel() {
    }

    EventModel(DocumentSnapshot documentSnapshot) {
        Map<String, Object> map = documentSnapshot.getData();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            event_map.put(entry.getKey(), entry.getValue());
        }
        event_map.put("document_id", documentSnapshot.getId());
    }

    EventModel(@NonNull byte[] value) {
        BufferedReader br = new BufferedReader(new StringReader(new String(value)));

        try {
            JSONObject jsonObject = new JSONObject(new String(value));
            Iterator<String> iterator = jsonObject.keys();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                event_map.put(key, jsonObject.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getByte() {
        String event_data_string = "";
        try {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : event_map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            event_data_string = jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return event_data_string.getBytes();
    }

    private String checkMapValueNull(String key) {
        Object value = event_map.get(key);
        if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }

    private Date checkMapDateNull(String key) {
        Object value = event_map.get(key);
        if (value != null) {
            return getDateFromString(value.toString());
        } else {
            return new Date();
        }
    }

    private Date getDateFromString(String date_string) {
        Date date;
        try {
            date = new Date(date_string);
        } catch (Exception e) {
            date = new Date();
        }
        return date;
    }
}
