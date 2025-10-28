package com.nistra.demy.platform.billing.interfaces.rest.transform;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.interfaces.rest.resources.BillingAccountResource;

import java.util.Collection;
import java.util.List;

public class BillingAccountResourceFromEntityAssembler {
    public static BillingAccountResource toResourceFromEntity(BillingAccount entity) {
        return new BillingAccountResource(
                entity.getId(),
                entity.getStudentId().studentId(),
                entity.getAcademyId().academyId(),
                InvoiceResourceFromEntityAssembler.toResourceListFromEntity(entity.getInvoices())
        );
    }

    public static List<BillingAccountResource> toResourceListFromEntity(Collection<BillingAccount> entities) {
        return entities.stream()
                .map(BillingAccountResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
    }
}
