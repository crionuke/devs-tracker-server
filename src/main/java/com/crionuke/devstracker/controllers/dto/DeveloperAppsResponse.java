package com.crionuke.devstracker.controllers.dto;

import com.crionuke.devstracker.actions.dto.DeveloperApp;

import java.util.List;

public class DeveloperAppsResponse {

    private final int count;
    private final List<DeveloperApp> apps;

    public DeveloperAppsResponse(List<DeveloperApp> apps) {
        this.count = apps.size();
        this.apps = apps;
    }

    public int getCount() {
        return count;
    }

    public List<DeveloperApp> getApps() {
        return apps;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(count=\"" + count + "\", " +
                "apps=" + apps + ")";
    }
}
