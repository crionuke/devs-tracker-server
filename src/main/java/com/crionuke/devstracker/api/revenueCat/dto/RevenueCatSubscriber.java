package com.crionuke.devstracker.api.revenueCat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueCatSubscriber {

    @JsonProperty("original_app_user_id")
    private String originalAppUserId;

    private Map<String, RevenueCatEntitlement> entitlements;

    public String getOriginalAppUserId() {
        return originalAppUserId;
    }

    public void setOriginalAppUserId(String originalAppUserId) {
        this.originalAppUserId = originalAppUserId;
    }

    public Map<String, RevenueCatEntitlement> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Map<String, RevenueCatEntitlement> entitlements) {
        this.entitlements = entitlements;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(originalAppUserId=\"" + originalAppUserId + "\", " +
                "entitlements=" + entitlements + ")";
    }
}
