package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;

public class Link {

    private final long id;
    private final Timestamp added;
    private final long appId;
    private final String title;
    private final String country;
    private final String url;

    public Link(long id, Timestamp added, long appId, String title, String country, String url) {
        this.id = id;
        this.added = added;
        this.appId = appId;
        this.title = title;
        this.country = country;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public Timestamp getAdded() {
        return added;
    }

    public long getAppId() {
        return appId;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + id + ", " +
                "added=\"" + added + "\", " +
                "appId=" + appId + ", " +
                "title=\"" + title + "\", " +
                "country=" + country + ", " +
                "url=\"" + url + "\")";
    }
}
