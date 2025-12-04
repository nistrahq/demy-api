package com.nistra.demy.platform.billing.domain.model.aggregates;

import com.nistra.demy.platform.billing.domain.model.commands.AssignInvoiceToBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.model.commands.CreateBillingAccountCommand;
import com.nistra.demy.platform.billing.domain.model.entities.Invoice;
import com.nistra.demy.platform.billing.domain.model.valueobjects.InvoiceStatus;
import com.nistra.demy.platform.billing.domain.model.valueobjects.InvoiceType;
import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;
import com.nistra.demy.platform.shared.domain.model.valueobjects.DniNumber;
import com.nistra.demy.platform.shared.domain.model.valueobjects.Money;
import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BillingAccount aggregate using the Arrange-Act-Assert (AAA) pattern.
 */
class BillingAccountTest {

    @Test
    @DisplayName("Should create BillingAccount from CreateBillingAccountCommand with empty invoices list")
    void shouldCreateBillingAccountFromCommand() {
        // Arrange
        StudentId studentId = new StudentId(1L);
        DniNumber dniNumber = new DniNumber("12345678");
        AcademyId academyId = new AcademyId(10L);

        // 👇 Ajusta la firma si tu comando tiene más campos
        CreateBillingAccountCommand command =
                new CreateBillingAccountCommand(studentId, dniNumber);

        // Act
        BillingAccount billingAccount = new BillingAccount(command, academyId);

        // Assert
        assertNotNull(billingAccount);
        assertEquals(studentId, billingAccount.getStudentId());
        assertEquals(dniNumber, billingAccount.getDniNumber());
        assertEquals(academyId, billingAccount.getAcademyId());
        assertNotNull(billingAccount.getInvoices(), "La lista de facturas no debe ser null");
        assertTrue(billingAccount.getInvoices().isEmpty(),
                "Al crear la cuenta no debe tener facturas asociadas");
    }

    @Test
    @DisplayName("Should assign an invoice to BillingAccount using AssignInvoiceToBillingAccountCommand")
    void shouldAssignInvoiceToBillingAccount() {
        // Arrange
        StudentId studentId = new StudentId(1L);
        DniNumber dniNumber = new DniNumber("12345678");
        AcademyId academyId = new AcademyId(10L);

        // 👇 Ajusta la firma del comando si tiene más campos
        CreateBillingAccountCommand createCommand =
                new CreateBillingAccountCommand(studentId, dniNumber);

        BillingAccount billingAccount = new BillingAccount(createCommand, academyId);

        Money amount = new Money(new BigDecimal("150.00"), Currency.getInstance("PEN")); // Ajusta si tu Money tiene otra firma
        LocalDate issueDate = LocalDate.of(2025, 1, 10);
        LocalDate dueDate = LocalDate.of(2025, 1, 31);

        // 👇 Ajusta los parámetros según tu record/command real
        AssignInvoiceToBillingAccountCommand assignCommand =
                new AssignInvoiceToBillingAccountCommand(
                        InvoiceType.STUDENT_MONTHLY_FEE,        // Usa un valor real de tu enum
                        amount,
                        "Matrícula ciclo 2025-I",
                        issueDate,
                        dueDate,
                        InvoiceStatus.PENDING,
                        100L// Estado inicial típico
                );

        // Act
        billingAccount.assignInvoice(assignCommand);

        // Assert
        List<Invoice> invoices = billingAccount.getInvoices();
        assertEquals(1, invoices.size(),
                "La cuenta de facturación debe tener exactamente una factura asignada");

        Invoice invoice = invoices.get(0);
        assertEquals(InvoiceType.STUDENT_MONTHLY_FEE, invoice.getInvoiceType());
        assertEquals(amount, invoice.getAmount());
        assertEquals("Matrícula ciclo 2025-I", invoice.getDescription());
        assertEquals(issueDate, invoice.getIssueDate());
        assertEquals(dueDate, invoice.getDueDate());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
        assertSame(billingAccount, invoice.getBillingAccount(),
                "La factura debe estar asociada a la misma BillingAccount");
    }

    @Test
    @DisplayName("Should return an unmodifiable list of invoices")
    void shouldReturnUnmodifiableInvoicesList() {
        // Arrange
        StudentId studentId = new StudentId(1L);
        DniNumber dniNumber = new DniNumber("12345678");
        AcademyId academyId = new AcademyId(10L);

        CreateBillingAccountCommand command =
                new CreateBillingAccountCommand(studentId, dniNumber);

        BillingAccount billingAccount = new BillingAccount(command, academyId);

        // Act
        List<Invoice> invoices = billingAccount.getInvoices();

        // Assert
        assertThrows(UnsupportedOperationException.class, invoices::clear,
                "La lista de facturas expuesta por el aggregate no debería permitir modificaciones externas");
    }
}