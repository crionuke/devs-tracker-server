package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;

public class App {

    private final long id;
    private final Timestamp added;
    private final long appleId;
    private final Timestamp releaseDate;
    private final long developerId;

    public App(long id, Timestamp added, long appleId, Timestamp releaseDate, long developerId) {
        this.id = id;
        this.added = added;
        this.appleId = appleId;
        this.releaseDate = releaseDate;
        this.developerId = developerId;
    }

    public long getId() {
        return id;
    }

    public Timestamp getAdded() {
        return added;
    }

    public long getAppleId() {
        return appleId;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public long getDeveloperId() {
        return developerId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + id + ", " +
                "added=\"" + added + "\", " +
                "appleId=" + appleId + ", " +
                "releaseDate=\"" + releaseDate + "\", " +
                "developerId=\"" + developerId + "\")";
    }
}
