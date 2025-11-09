package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.pdf.formatters;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.accountingfinance.domain.model.valueobjects.TransactionType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransactionPdfDataFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Map<String, BigDecimal> calculateTotalsByCurrency(List<Transaction> transactions) {
        Map<String, BigDecimal> totals = new HashMap<>();
        transactions.forEach(t -> {
            String currency = t.getAmount().currency().toString();
            BigDecimal amount = t.getAmount().amount();
            totals.merge(currency, amount, BigDecimal::add);
        });
        return totals;
    }

    public Map<String, BigDecimal> calculateTotalsByCategory(List<Transaction> transactions) {
        Map<String, BigDecimal> totals = new HashMap<>();
        transactions.forEach(t -> {
            String category = t.getTransactionCategory().name();
            BigDecimal amount = t.getAmount().amount();
            totals.merge(category, amount, BigDecimal::add);
        });
        return totals;
    }

    public Map<String, BigDecimal> calculateExpenseTotalsByCategory(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> TransactionType.EXPENSE.equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionCategory().name(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                t -> t.getAmount().amount(),
                                BigDecimal::add
                        )
                ));
    }

    public Map<String, BigDecimal> calculateIncomeTotalsOverTime(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> TransactionType.INCOME.equals(t.getTransactionType()))
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().format(DATE_FORMATTER),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                t -> t.getAmount().amount(),
                                BigDecimal::add
                        )
                ));
    }

    public Map<String, BigDecimal> calculateTotalsOverTime(List<Transaction> transactions) {
        Map<String, BigDecimal> totals = new HashMap<>();
        transactions.forEach(t -> {
            String dateKey = t.getTransactionDate().format(DATE_FORMATTER);
            BigDecimal amount = t.getAmount().amount();
            totals.merge(dateKey, amount, BigDecimal::add);
        });
        return totals;
    }

    public String formatAmount(BigDecimal amount, String currency) {
        return String.format("%,.2f %s", amount, currency);
    }

    public String formatDate(java.time.LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}

