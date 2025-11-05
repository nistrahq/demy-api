package com.nistra.demy.platform.accountingfinance.interfaces.rest.controllers;

import com.nistra.demy.platform.accountingfinance.domain.model.queries.GetAllTransactionsQuery;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionCategory;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionMethod;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionType;
import com.nistra.demy.platform.accountingfinance.domain.services.TransactionCommandService;
import com.nistra.demy.platform.accountingfinance.domain.services.TransactionQueryService;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.resources.*;
import com.nistra.demy.platform.accountingfinance.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(
            summary = "Get All Transactions",
            description = "Retrieves all transactions registered in the system. " +
                    "Optional query parameters can be used to filter the results by category, method, or type."
    )
    public ResponseEntity<List<TransactionResource>> getAllTransactions(
            @Parameter(
                    description = "Filter by transaction category (e.g., STUDENT_ENROLLMENT, TEACHER_SALARY)",
                    schema = @Schema(implementation = TransactionCategory.class)
            )
            @RequestParam(required = false) String category,
            @Parameter(
                    description = "Filter by transaction method (e.g., CASH, CREDIT_CARD)",
                    schema = @Schema(implementation = TransactionMethod.class)
            )
            @RequestParam(required = false) String method,
            @Parameter(
                    description = "Filter by transaction type (e.g., INCOME, EXPENSE)",
                    schema = @Schema(implementation = TransactionType.class)
            )
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
