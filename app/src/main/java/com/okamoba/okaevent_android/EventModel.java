package com.okamoba.okaevent_android;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by keiichilo on 2018/02/18.
 */

public class EventModel {
    @NonNull private String name = "";
    @NonNull private String text = "";
    @NonNull private String address = "";
    @NonNull private Date start_datetime = new Date();
    @NonNull private Date end_datetime = new Date();
    @NonNull private String url = "";
    @NonNull private String author = "";

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    @NonNull
    public String getAddress() {
        return address;
    }

    public void setAddress(@NonNull String address) {
        this.address = address;
    }

    @NonNull
    public Date getStart_datetime() {
        return start_datetime;
    }

    public void setStart_datetime(@NonNull Date start_datetime) {
        this.start_datetime = start_datetime;
    }

    @NonNull
    public Date getEnd_datetime() {
        return end_datetime;
    }

    public void setEnd_datetime(@NonNull Date end_datetime) {
        this.end_datetime = end_datetime;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getAuthor() {
        return author;
    }

    public void setAuthor(@NonNull String uid) {
        this.author = uid;
    }

    public Map<String, Object> getEvent() {
        Map<String, Object> event_map = new HashMap<>();

        event_map.put("name", name);
        event_map.put("text", text);
        event_map.put("address", address);
        event_map.put("start_datetime", start_datetime);
        event_map.put("end_datetime", end_datetime);
        event_map.put("url", url);
        event_map.put("uid", author);

        return event_map;
    }
}
