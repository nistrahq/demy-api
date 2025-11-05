package com.nistra.demy.platform.billing.domain.services;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.domain.model.commands.AssignInvoiceToBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.model.commands.CreateBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.model.commands.DeleteInvoiceCommand;
import com.nistra.demy.platform.billing.domain.model.commands.MarkInvoiceAsPaidCommand;
import com.nistra.demy.platform.billing.domain.model.entities.Invoice;

import java.util.Optional;

public interface BillingAccountCommandService {
    Optional<BillingAccount> handle(CreateBillingAccountCommand command);

    Optional<BillingAccount> handle(AssignInvoiceToBillingAccountCommand command);

    Optional<Invoice> handle(MarkInvoiceAsPaidCommand command);

    void handle(DeleteInvoiceCommand command);

}
