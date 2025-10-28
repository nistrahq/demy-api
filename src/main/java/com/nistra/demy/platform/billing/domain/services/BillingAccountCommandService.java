package com.nistra.demy.platform.billing.domain.services;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.domain.model.commands.AssignInvoiceCommand;
import com.nistra.demy.platform.billing.domain.model.commands.CreateBillingAccountCommand;

import java.util.Optional;

public interface BillingAccountCommandService {
    Optional<BillingAccount> handle(CreateBillingAccountCommand command);

    Optional<BillingAccount> handle(AssignInvoiceCommand command);
}
