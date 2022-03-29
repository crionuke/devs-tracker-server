package com.crionuke.devstracker.api.revenueCat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueCatResponse {

    private RevenueCatSubscriber subscriber;

    public RevenueCatSubscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(RevenueCatSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    public boolean hasActiveEntitlement() {
        for (RevenueCatEntitlement entitlement : subscriber.getEntitlements().values()) {
            if (entitlement.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(subscriber=" + subscriber + ", " +
                "hasActiveEntitlement=" + hasActiveEntitlement() + ")";
    }
}
