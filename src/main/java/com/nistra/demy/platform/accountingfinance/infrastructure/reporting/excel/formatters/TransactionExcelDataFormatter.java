package com.nistra.demy.platform.accountingfinance.infrastructure.reporting.excel.formatters;

import com.nistra.demy.platform.accountingfinance.domain.model.aggregates.Transaction;
import com.nistra.demy.platform.shared.domain.model.valueobjects.Money;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TransactionExcelDataFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public double getAmountValue(Money money) {
        return money.amount().doubleValue();
    }

    public String formatDescription(String description) {
        return description != null ? description : "-";
    }

    public Map<Currency, Money> calculateTotalsByCurrency(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptyMap();
        }

        return transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getAmount().currency(),
                        Collectors.reducing(
                                null,
                                Transaction::getAmount,
                                (m1, m2) -> {
                                    if (m1 == null) return m2;
                                    if (m2 == null) return m1;
                                    return m1.add(m2);
                                }
                        )
                ));
    }

    public Set<Currency> getUniqueCurrencies(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return Collections.emptySet();
        }

        return transactions.stream()
                .map(t -> t.getAmount().currency())
                .collect(Collectors.toCollection(() ->
                    new TreeSet<>(Comparator.comparing(Currency::getCurrencyCode))
                ));
    }
}

