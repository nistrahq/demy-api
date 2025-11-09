package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.formatters;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TransactionExcelDataFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public double formatAmount(BigDecimal amount) {
        return amount.doubleValue();
    }

    public String formatDescription(String description) {
        return description != null ? description : "-";
    }

    public BigDecimal calculateTotalAmount(List<Transaction> transactions) {
        return transactions.stream()
                .map(t -> t.getAmount().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

