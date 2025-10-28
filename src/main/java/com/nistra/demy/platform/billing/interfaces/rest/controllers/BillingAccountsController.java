package com.nistra.demy.platform.billing.interfaces.rest.controllers;

import com.nistra.demy.platform.billing.domain.model.queries.GetAllInvoicesByBillingAccountIdQuery;
import com.nistra.demy.platform.billing.domain.model.queries.GetAllInvoicesByStudentIdQuery;
import com.nistra.demy.platform.billing.domain.model.queries.GetBillingAccountByIdQuery;
import com.nistra.demy.platform.billing.domain.services.BillingAccountCommandService;
import com.nistra.demy.platform.billing.domain.services.BillingAccountQueryService;
import com.nistra.demy.platform.billing.interfaces.rest.resources.AssignInvoiceResource;
import com.nistra.demy.platform.billing.interfaces.rest.resources.BillingAccountResource;
import com.nistra.demy.platform.billing.interfaces.rest.resources.CreateBillingAccountResource;
import com.nistra.demy.platform.billing.interfaces.rest.resources.InvoiceResource;
import com.nistra.demy.platform.billing.interfaces.rest.transform.AssignInvoiceCommandFromResourceAssembler;
import com.nistra.demy.platform.billing.interfaces.rest.transform.BillingAccountResourceFromEntityAssembler;
import com.nistra.demy.platform.billing.interfaces.rest.transform.CreateBillingAccountCommandFromResourceAssembler;
import com.nistra.demy.platform.billing.interfaces.rest.transform.InvoiceResourceFromEntityAssembler;
import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/billing-accounts", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Billing Accounts", description = "Endpoints for billing accounts")
public class BillingAccountsController {

    private final BillingAccountCommandService billingAccountCommandService;
    private final BillingAccountQueryService billingAccountQueryService;

    public  BillingAccountsController(
            BillingAccountCommandService billingAccountCommandService,
            BillingAccountQueryService billingAccountQueryService
    ) {
        this.billingAccountCommandService = billingAccountCommandService;
        this.billingAccountQueryService = billingAccountQueryService;
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
    public ResponseEntity<BillingAccountResource> assignInvoiceToBillingAccount(@PathVariable Long billingAccountId, @RequestBody AssignInvoiceResource resource) {
        var assignInvoiceCommand = AssignInvoiceCommandFromResourceAssembler.toCommandFromResource(billingAccountId, resource);
        var billingAccount = billingAccountCommandService.handle(assignInvoiceCommand);
        if (billingAccount.isEmpty()) return ResponseEntity.badRequest().build();
        var billingAccountEntity = billingAccount.get();
        var billingAccountResource = BillingAccountResourceFromEntityAssembler.toResourceFromEntity(billingAccountEntity);
        return new ResponseEntity<>(billingAccountResource, HttpStatus.CREATED);
    }

    @GetMapping("/{billingAccountId}")
    public ResponseEntity<BillingAccountResource> getBillingAccountById(@PathVariable Long billingAccountId) {
        var getBillingAccountByIdQuery = new GetBillingAccountByIdQuery(billingAccountId);
        var billingAccount = billingAccountQueryService.handle(getBillingAccountByIdQuery);
        if (billingAccount.isEmpty()) return ResponseEntity.notFound().build();
        var billingAccountEntity = billingAccount.get();
        var billingAccountResource = BillingAccountResourceFromEntityAssembler.toResourceFromEntity(billingAccountEntity);
        return new ResponseEntity<>(billingAccountResource, HttpStatus.OK);
    }

    @GetMapping("/{billingAccountId}/invoices")
    public ResponseEntity<List<InvoiceResource>> getAllInvoicesByAccountId(@PathVariable Long billingAccountId) {
        var qetAllInvoicesByBillingAccountIdQuery = new GetAllInvoicesByBillingAccountIdQuery(billingAccountId);
        var invoices = billingAccountQueryService.handle(qetAllInvoicesByBillingAccountIdQuery);
        if (invoices.isEmpty()) return ResponseEntity.noContent().build();
        var invoiceResources = InvoiceResourceFromEntityAssembler.toResourceListFromEntity(invoices);
        return new ResponseEntity<>(invoiceResources, HttpStatus.OK);
    }

    @GetMapping("/student/{studentId}/invoices")
    public ResponseEntity<List<InvoiceResource>> getAllInvoicesByStudentId(@PathVariable Long studentId) {
        var getAllInvoicesByStudentIdQuery = new GetAllInvoicesByStudentIdQuery(new StudentId(studentId));
        var invoices = billingAccountQueryService.handle(getAllInvoicesByStudentIdQuery);
        if (invoices.isEmpty()) return ResponseEntity.noContent().build();
        var invoiceResources = InvoiceResourceFromEntityAssembler.toResourceListFromEntity(invoices);
        return new ResponseEntity<>(invoiceResources, HttpStatus.OK);
    }
}
