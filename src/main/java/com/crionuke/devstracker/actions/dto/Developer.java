package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;

public class Developer {

    private final long id;
    private final Timestamp added;
    private final long appleId;
    private final String name;

    public Developer(long id, Timestamp added, long appleId, String name) {
        this.id = id;
        this.added = added;
        this.appleId = appleId;
        this.name = name;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + id + ", " +
                "added=" + added + ", " +
                "appleId=" + appleId + ", " +
                "name=\"" + name + "\")";
    }
}
