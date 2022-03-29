package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;

public class TrackedDeveloper {

    private final Timestamp added;
    private final long appleId;
    private final String name;
    private final long count;

    public TrackedDeveloper(Timestamp added, long appleId, String name, long count) {
        this.added = added;
        this.appleId = appleId;
        this.name = name;
        this.count = count;
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

    public long getCount() {
        return count;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(added=\"" + added + "\", " +
                "appleId=" + appleId + ", " +
                "name=\"" + name + "\", " +
                "count=" + count + ")";
    }
}
