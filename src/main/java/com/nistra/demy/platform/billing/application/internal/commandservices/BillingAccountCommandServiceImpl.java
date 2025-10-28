package com.nistra.demy.platform.billing.application.internal.commandservices;

import com.nistra.demy.platform.billing.application.internal.outboundservices.acl.ExternalIamService;
import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.domain.model.commands.AssignInvoiceToBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.model.commands.CreateBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.services.BillingAccountCommandService;
import com.nistra.demy.platform.billing.infrastructure.persistence.jpa.repositories.BillingAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BillingAccountCommandServiceImpl implements BillingAccountCommandService {

    private final BillingAccountRepository billingAccountRepository;
    private final ExternalIamService externalIamService;

    public BillingAccountCommandServiceImpl(
            BillingAccountRepository billingAccountRepository,
            ExternalIamService externalIamService
    ) {
        this.billingAccountRepository = billingAccountRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    public Optional<BillingAccount> handle(CreateBillingAccountCommand command) {
        var academyId = externalIamService.fetchCurrentAcademyId()
                .orElseThrow(() -> new RuntimeException("No academy found"));
        var billingAccount = new BillingAccount(command, academyId);
        try {
            billingAccountRepository.save(billingAccount);
            return Optional.of(billingAccount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create billing account: %s".formatted(e.getMessage()));
        }
    }

    @Override
    public Optional<BillingAccount> handle(AssignInvoiceToBillingAccountCommand command) {
        var billingAccount = billingAccountRepository.findById(command.billingAccountId())
                .orElseThrow(() -> new RuntimeException("Billing account not found"));
        billingAccount.assignInvoice(command);
        try {
            billingAccountRepository.save(billingAccount);
            return Optional.of(billingAccount);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign invoice: %s".formatted(e.getMessage()));
        }
    }
}
