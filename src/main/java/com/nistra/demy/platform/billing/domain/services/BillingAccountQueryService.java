package com.nistra.demy.platform.billing.domain.services;

import com.nistra.demy.platform.billing.domain.model.entities.Invoice;
import com.nistra.demy.platform.billing.domain.model.queries.GetAllInvoicesByBillingAccountIdQuery;
import com.nistra.demy.platform.billing.domain.model.queries.GetAllInvoicesByStudentIdQuery;

import java.util.List;

public interface BillingAccountQueryService {
    List<Invoice> handle(GetAllInvoicesByBillingAccountIdQuery query);

    List<Invoice> handle(GetAllInvoicesByStudentIdQuery query);
}
