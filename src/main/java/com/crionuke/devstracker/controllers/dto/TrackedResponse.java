package com.crionuke.devstracker.controllers.dto;

import com.crionuke.devstracker.actions.dto.TrackedDeveloper;

import java.util.List;

public class TrackedResponse {

    private final int count;
    private final List<TrackedDeveloper> developers;

    public TrackedResponse(int count, List<TrackedDeveloper> developers) {
        this.count = count;
        this.developers = developers;
    }

    public int getCount() {
        return count;
    }

    public List<TrackedDeveloper> getDevelopers() {
        return developers;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(count=\"" + count + "\", " +
                "developers=" + developers + ")";
    }
}
