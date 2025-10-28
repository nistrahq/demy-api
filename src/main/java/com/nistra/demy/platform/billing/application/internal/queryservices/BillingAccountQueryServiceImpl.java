package com.nistra.demy.platform.billing.application.internal.queryservices;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.domain.model.entities.Invoice;
import com.nistra.demy.platform.billing.domain.model.queries.GetAllInvoicesByBillingAccountIdQuery;
import com.nistra.demy.platform.billing.domain.model.queries.GetAllInvoicesByStudentIdQuery;
import com.nistra.demy.platform.billing.domain.model.queries.GetBillingAccountByIdQuery;
import com.nistra.demy.platform.billing.domain.services.BillingAccountQueryService;
import com.nistra.demy.platform.billing.infrastructure.persistence.jpa.repositories.BillingAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BillingAccountQueryServiceImpl implements BillingAccountQueryService {

    private final BillingAccountRepository billingAccountRepository;

    public BillingAccountQueryServiceImpl(BillingAccountRepository billingAccountRepository) {
        this.billingAccountRepository = billingAccountRepository;
    }

    @Override
    public Optional<BillingAccount> handle(GetBillingAccountByIdQuery query) {
        return billingAccountRepository.findById(query.billingAccountId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> handle(GetAllInvoicesByBillingAccountIdQuery query) {
        var billingAccount = billingAccountRepository.findById(query.billingAccountId())
                .orElseThrow(() -> new RuntimeException("Billing account not found for id: %s".formatted(query.billingAccountId())));
        return billingAccount.getInvoices();
    }

    @Override
    public List<Invoice> handle(GetAllInvoicesByStudentIdQuery query) {
        var billingAccount = billingAccountRepository.findByStudentId(query.studentId())
                .orElseThrow(() -> new RuntimeException("Billing account not found for student id: %s".formatted(query.studentId())));
        return billingAccount.getInvoices();
    }
}
