package com.nistra.demy.platform.billing.domain.model.entities;

import com.nistra.demy.platform.billing.domain.model.commands.AssignInvoiceCommand;
import com.nistra.demy.platform.billing.domain.model.valueobjects.BillingAccountId;
import com.nistra.demy.platform.billing.domain.model.valueobjects.InvoiceStatus;
import com.nistra.demy.platform.billing.domain.model.valueobjects.InvoiceType;
import com.nistra.demy.platform.shared.domain.model.entities.AuditableModel;
import com.nistra.demy.platform.shared.domain.model.valueobjects.Money;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;

@Entity
public class Invoice extends AuditableModel {

    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;

    @Embedded
    private Money amount;

    private String description;

    private LocalDate issueDate;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Embedded
    private BillingAccountId billingAccountId;

    protected Invoice() {
    }
}
