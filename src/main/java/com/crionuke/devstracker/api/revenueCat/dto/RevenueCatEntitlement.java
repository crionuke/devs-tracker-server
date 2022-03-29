package com.crionuke.devstracker.api.revenueCat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RevenueCatEntitlement {
    private static final Logger logger = LoggerFactory.getLogger(RevenueCatEntitlement.class);

    @JsonProperty("expires_date")
    private Instant expiresDate;

    @JsonProperty("product_identifier")
    private String productIdentifier;

    @JsonProperty("purchase_date")
    private Instant purchaseDate;

    public Instant getExpiresDate() {
        return expiresDate;
    }

    public void setExpiresDate(String expiresDate) {
        this.expiresDate = Instant.parse(expiresDate);
    }

    public String getProductIdentifier() {
        return productIdentifier;
    }

    public void setProductIdentifier(String productIdentifier) {
        this.productIdentifier = productIdentifier;
    }

    public Instant getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = Instant.parse(purchaseDate);
    }

    public boolean isActive() {
        return expiresDate.compareTo(Instant.now()) > 0;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(expiresDate=" + expiresDate + ", " +
                "productIdentifier=" + productIdentifier + ", " +
                "purchaseDate=" + purchaseDate + ", " +
                "isActive=" + isActive() + ", now=" + Instant.now() + ")";
    }
}
