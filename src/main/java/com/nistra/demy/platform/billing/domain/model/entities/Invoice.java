package com.nistra.demy.platform.billing.domain.model.entities;

import com.nistra.demy.platform.billing.domain.model.valueobjects.InvoiceType;
import com.nistra.demy.platform.shared.domain.model.entities.AuditableModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Invoice extends AuditableModel {

    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
}
