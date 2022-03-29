package com.crionuke.devstracker.controllers.dto;

import java.util.List;

public class SearchRequest {

    private List<String> countries;
    private String term;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(countries=\"" + countries + "\", term=\"" + term + "\")";
    }
}
