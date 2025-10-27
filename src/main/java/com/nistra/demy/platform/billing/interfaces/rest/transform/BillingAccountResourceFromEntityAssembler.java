package com.nistra.demy.platform.billing.interfaces.rest.transform;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.interfaces.rest.resources.BillingAccountResource;

public class BillingAccountResourceFromEntityAssembler {
    public static BillingAccountResource toResourceFromEntity(BillingAccount entity) {
        return new BillingAccountResource(
                entity.getId(),
                entity.getStudentId().studentId(),
                entity.getAcademyId().academyId(),
                InvoiceResourceFromEntityAssembler.toResourceListFromEntity(entity.getInvoices())
        );
    }
}
