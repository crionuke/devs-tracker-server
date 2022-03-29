package com.crionuke.devstracker.actions.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeveloperApp {

    private final long appleId;
    private final Timestamp releaseDate;
    private final Map<String, String> translations;

    public DeveloperApp(long appleId, Timestamp releaseDate) {
        this.appleId = appleId;
        this.releaseDate = releaseDate;
        translations = new HashMap<>();
    }

    public long getAppleId() {
        return appleId;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void addTranslation(String country, String title) {
        translations.put(country, title);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(appleId=" + appleId + ", " +
                "releaseDate=\"" + releaseDate + "\", translations=" + translations + ")";
    }
}
