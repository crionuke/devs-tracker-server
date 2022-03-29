package com.crionuke.devstracker.controllers.dto;

import com.crionuke.devstracker.services.dto.SearchDeveloper;

import java.util.List;

public class SearchResponse {

    private final int count;
    private final List<SearchDeveloper> developers;

    public SearchResponse(int count, List<SearchDeveloper> developers) {
        this.count = count;
        this.developers = developers;
    }

    public int getCount() {
        return count;
    }

    public List<SearchDeveloper> getDevelopers() {
        return developers;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(count=\"" + count + "\", " +
                "developers=" + developers + ")";
    }
}
