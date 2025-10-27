package com.nistra.demy.platform.billing.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record BillingAccountId(
        @Column(nullable = false)
        Long id
) {
    public BillingAccountId() {
        this(null);
    }

    public BillingAccountId {
        if (id == null || id <= 0)
            throw new IllegalArgumentException("Billing Account ID must be greater than zero");
    }
}
