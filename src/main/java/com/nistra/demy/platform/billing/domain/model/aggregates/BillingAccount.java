package com.nistra.demy.platform.billing.domain.model.aggregates;

import com.nistra.demy.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
public class BillingAccount extends AuditableAbstractAggregateRoot<BillingAccount> {

    @Embedded
    @Getter
    private StudentId studentId;
}
