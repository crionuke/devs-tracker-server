package com.crionuke.devstracker.actions.dto;

public class AppLink {

    private final String title;
    private final String country;
    private final String url;

    public AppLink(String title, String country, String url) {
        this.title = title;
        this.country = country;
        this.url = url;
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
        return getClass().getSimpleName() + "(title=" + title + ", " +
                "country=\"" + country + "\", url=\"" + url + "\")";
    }
}
