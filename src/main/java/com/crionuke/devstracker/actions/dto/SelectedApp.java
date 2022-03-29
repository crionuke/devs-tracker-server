package com.crionuke.devstracker.actions.dto;

public class SelectedApp {

    private final SearchApp searchApp;
    private final App app;

    public SelectedApp(SearchApp searchApp, App app) {
        this.searchApp = searchApp;
        this.app = app;
    }

    public SearchApp getSearchApp() {
        return searchApp;
    }

    public App getApp() {
        return app;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(searchApp=" + searchApp + ", app=" + app + ")";
    }
}
