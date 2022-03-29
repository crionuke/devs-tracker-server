package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;

public class Alias {

    private final long id;
    private final Timestamp added;
    private final String token;
    private final long userId;

    public Alias(long id, Timestamp added, String token, long userId) {
        this.id = id;
        this.added = added;
        this.token = token;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public Timestamp getAdded() {
        return added;
    }

    public String getToken() {
        return token;
    }

    public long getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(id=" + id + ", " +
                "added=\"" + added + "\", " +
                "token=\"" + "..." + token.substring(Math.max(0, token.length() - 8)) + "\", " +
                "userId=" + "..." + userId + ")";
    }
}
