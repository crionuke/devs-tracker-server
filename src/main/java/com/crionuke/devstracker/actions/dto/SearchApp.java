package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;

public class SearchApp {

    private final long appleId;
    private final String title;
    private final String url;
    private final Timestamp releaseDate;

    public SearchApp(long appleId, String title, String url, Timestamp releaseDate) {
        this.appleId = appleId;
        this.title = title;
        this.url = url;
        this.releaseDate = releaseDate;
    }

    public long getAppleId() {
        return appleId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(appleId=" + appleId + ", " +
                "title=\"" + title + "\", " +
                "url=\"" + url + "\", " +
                "releaseDate=\"" + releaseDate + "\")";
    }
}
