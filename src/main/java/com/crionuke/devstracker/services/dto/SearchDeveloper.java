package com.crionuke.devstracker.services.dto;

import java.util.Objects;

public class SearchDeveloper {

    private final long appleId;
    private final String name;

    public SearchDeveloper(long appleId, String name) {
        this.appleId = appleId;
        this.name = name;
    }

    public long getAppleId() {
        return appleId;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchDeveloper that = (SearchDeveloper) o;
        return appleId == that.appleId &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appleId, name);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(name=\"" + name + "\", id=" + appleId + ")";
    }
}
