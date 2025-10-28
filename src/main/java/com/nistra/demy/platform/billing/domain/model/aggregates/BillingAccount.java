package com.nistra.demy.platform.billing.domain.model.aggregates;

import com.nistra.demy.platform.billing.domain.model.commands.AssignInvoiceCommand;
import com.nistra.demy.platform.billing.domain.model.commands.CreateBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.model.entities.Invoice;
import com.nistra.demy.platform.billing.domain.model.valueobjects.AccountStatus;
import com.nistra.demy.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;
import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.*;

@Entity
public class BillingAccount extends AuditableAbstractAggregateRoot<BillingAccount> {

    @Embedded
    @Getter
    private StudentId studentId;

    @OneToMany(mappedBy = "billingAccount", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Embedded
    @Getter
    private AcademyId academyId;

    protected BillingAccount() {}

    public BillingAccount(CreateBillingAccountCommand command, AcademyId academyId) {
        this.studentId = command.studentId();
        this.invoices = new ArrayList<>();
        this.status = AccountStatus.ACTIVE;
        this.academyId = academyId;
    }

    public void assignInvoice(AssignInvoiceCommand command) {
        var invoice = new Invoice(
                command.invoiceType(),
                command.amount(),
                command.description(),
                command.issueDate(),
                command.dueDate(),
                command.status(),
                this
        );
        invoices.add(invoice);
    }

    public List<Invoice> getInvoices() {
        return Collections.unmodifiableList(invoices);
    }
}
