package com.okamoba.okaevent_android;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by keiichilo on 2018/02/18.
 */

public class EventModel {
    private Map<String, Object> event_map = new HashMap<>();

    private String name;
    private String text;
    private String address;
    private LocalDateTime start_datetime;
    private LocalDateTime end_datetime;
    private String url;
    private String uid;

    public String getName() {
        return (String)event_map.get("name");
    }

    public String getText() {
        return (String)event_map.get("text");
    }

    public String getAddress() {
        return (String)event_map.get("address");
    }

    public Date getStart_datetime() {
        return (Date)event_map.get("start_datetime");
    }

    public Date getEnd_datetime() {
        return (Date)event_map.get("end_datetime");
    }

    public String getUrl() {
        return (String)event_map.get("url");
    }

    public String getUid() {
        return (String)event_map.get("url");
    }

    public void setName(String name) {
        event_map.put("name", name);
    }

    public void setText(String text) {
        event_map.put("text", text);
    }

    public void setAddress(String address) {
        event_map.put("address", address);
    }

    public void setStart_datetime(Date start_datetime) {
        event_map.put("start_datetime", start_datetime);
    }

    public void setEnd_datetime(Date end_datetime) {
        event_map.put("end_datetime", end_datetime);
    }

    public void setUrl(String url) {
        event_map.put("url", url);
    }

    public void setUid(String uid) {
        event_map.put("uid", uid);
    }

    public Map<String, Object> getEvent() {
        return event_map;
    }
}
