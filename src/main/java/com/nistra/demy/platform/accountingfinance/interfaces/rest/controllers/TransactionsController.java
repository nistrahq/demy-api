package com.nistra.demy.platform.accountingfinance.interfaces.rest.controllers;

import com.nistra.demy.platform.accountingfinance.domain.services.TransactionCommandService;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.resources.RegisterTransactionResource;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.resources.TransactionResource;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.transform.RegisterTransactionCommandFromResourceAssembler;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.transform.TransactionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/transactions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Accounting and Financial Transactions", description = "Endpoints for managing accounting and financial transactions")
public class TransactionsController {

    private final TransactionCommandService transactionCommandService;

    public TransactionsController(
            TransactionCommandService transactionCommandService
    ) {
        this.transactionCommandService = transactionCommandService;
    }

    @PostMapping
    public ResponseEntity<TransactionResource> registerTransaction(@RequestBody RegisterTransactionResource resource) {
        var registerTransactionCommand = RegisterTransactionCommandFromResourceAssembler.toCommandFromResource(resource);
        var transaction = transactionCommandService.handle(registerTransactionCommand);
        if (transaction.isEmpty()) return ResponseEntity.badRequest().build();
        var transactionEntity = transaction.get();
        var transactionResource = TransactionResourceFromEntityAssembler.toResourceFromEntity(transactionEntity);
        return new ResponseEntity<>(transactionResource, HttpStatus.CREATED);
    }
}
