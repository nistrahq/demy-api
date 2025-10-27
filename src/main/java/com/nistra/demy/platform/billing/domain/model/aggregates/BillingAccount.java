package com.nistra.demy.platform.billing.domain.model.aggregates;

import com.nistra.demy.platform.billing.domain.model.entities.Invoice;
import com.nistra.demy.platform.billing.domain.model.valueobjects.AccountStatus;
import com.nistra.demy.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;
import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;

import java.util.Set;

@Entity
public class BillingAccount extends AuditableAbstractAggregateRoot<BillingAccount> {

    @Embedded
    @Getter
    private StudentId studentId;

    @Getter
    private Set<Invoice> invoices;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Embedded
    @Getter
    private AcademyId academyId;

    protected BillingAccount() {}


}
