package com.crionuke.devstracker.api.apple.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleResponse {
    private int resultCount;
    private List<AppleResult> results;

    public AppleResponse() {
        resultCount = 0;
        results = new ArrayList<>();
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public List<AppleResult> getResults() {
        return results;
    }

    public void setResults(List<AppleResult> results) {
        this.results = results;
    }
}