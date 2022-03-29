package com.crionuke.devstracker.controllers.dto;

import com.crionuke.devstracker.actions.dto.AppLink;

import java.util.List;

public class AppLinksResponse {

    private final int count;
    private final List<AppLink> links;

    public AppLinksResponse(List<AppLink> links) {
        this.count = links.size();
        this.links = links;
    }

    public int getCount() {
        return count;
    }

    public List<AppLink> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(count=\"" + count + "\", links=" + links + ")";
    }
}
