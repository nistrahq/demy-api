package com.nistra.demy.platform.accountingfinance.interfaces.rest.controllers;

import com.nistra.demy.platform.accountingfinance.domain.model.queries.GetAllTransactionsQuery;
import com.nistra.demy.platform.accountingfinance.domain.services.TransactionCommandService;
import com.nistra.demy.platform.accountingfinance.domain.services.TransactionQueryService;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.resources.*;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/transactions", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Accounting and Financial Transactions", description = "Endpoints for managing accounting and financial transactions")
public class TransactionsController {

    private final TransactionCommandService transactionCommandService;
    private final TransactionQueryService transactionQueryService;

    public TransactionsController(
            TransactionCommandService transactionCommandService,
            TransactionQueryService transactionQueryService
    ) {
        this.transactionCommandService = transactionCommandService;
        this.transactionQueryService = transactionQueryService;
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

    @GetMapping
    public ResponseEntity<List<TransactionResource>> getAllTransactions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String type
    ) {
        var getAllTransactionsQuery = new GetAllTransactionsQuery(
                TransactionCategoryFromResourceAssembler.toValueObjectFromString(category),
                TransactionMethodResourceAssembler.toValueObjectFromString(method),
                TransactionTypeFromResourceAssembler.toValueObjectFromString(type)
        );
        var transactions = transactionQueryService.handle(getAllTransactionsQuery);
        if (transactions.isEmpty()) return ResponseEntity.ok().body(List.of());
        var transactionResources = TransactionResourceFromEntityAssembler.toResourcesFromEntities(transactions);
        return new ResponseEntity<>(transactionResources, HttpStatus.OK);
    }
}
