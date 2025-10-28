package com.nistra.demy.platform.billing.interfaces.rest.controllers;

import com.nistra.demy.platform.billing.domain.services.BillingAccountCommandService;
import com.nistra.demy.platform.billing.interfaces.rest.resources.AssignInvoiceResource;
import com.nistra.demy.platform.billing.interfaces.rest.resources.BillingAccountResource;
import com.nistra.demy.platform.billing.interfaces.rest.resources.CreateBillingAccountResource;
import com.nistra.demy.platform.billing.interfaces.rest.transform.AssignInvoiceCommandFromResourceAssembler;
import com.nistra.demy.platform.billing.interfaces.rest.transform.BillingAccountResourceFromEntityAssembler;
import com.nistra.demy.platform.billing.interfaces.rest.transform.CreateBillingAccountCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/billing-accounts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Billing Accounts", description = "Endpoints for billing accounts")
public class BillingAccountsController {

    private final BillingAccountCommandService billingAccountCommandService;

    public  BillingAccountsController(
            BillingAccountCommandService billingAccountCommandService
    ) {
        this.billingAccountCommandService = billingAccountCommandService;
    }

    @PostMapping
    public ResponseEntity<BillingAccountResource> createBillingAccount(@RequestBody CreateBillingAccountResource resource) {
        var createBillingAccountCommand = CreateBillingAccountCommandFromResourceAssembler.toCommandFromResource(resource);
        var billingAccount = billingAccountCommandService.handle(createBillingAccountCommand);
        if (billingAccount.isEmpty()) return ResponseEntity.badRequest().build();
        var billingAccountEntity = billingAccount.get();
        var billingAccountResource = BillingAccountResourceFromEntityAssembler.toResourceFromEntity(billingAccountEntity);
        return new ResponseEntity<>(billingAccountResource, HttpStatus.CREATED);
    }

    @PostMapping("/{billingAccountId}/invoices")
    public ResponseEntity<BillingAccountResource> assignInvoice(@PathVariable Long billingAccountId, @RequestBody AssignInvoiceResource resource) {
        var assignInvoiceCommand = AssignInvoiceCommandFromResourceAssembler.toCommandFromResource(billingAccountId, resource);
        var billingAccount = billingAccountCommandService.handle(assignInvoiceCommand);
        if (billingAccount.isEmpty()) return ResponseEntity.badRequest().build();
        var billingAccountEntity = billingAccount.get();
        var billingAccountResource = BillingAccountResourceFromEntityAssembler.toResourceFromEntity(billingAccountEntity);
        return new ResponseEntity<>(billingAccountResource, HttpStatus.CREATED);
    }
}
