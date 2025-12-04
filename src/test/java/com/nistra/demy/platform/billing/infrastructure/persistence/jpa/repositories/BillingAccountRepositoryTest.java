package com.nistra.demy.platform.billing.infrastructure.persistence.jpa.repositories;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import com.nistra.demy.platform.billing.domain.model.commands.CreateBillingAccountCommand;
import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;
import com.nistra.demy.platform.shared.domain.model.valueobjects.DniNumber;
import com.nistra.demy.platform.shared.domain.model.valueobjects.StudentId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("BillingAccountRepository Integration Tests")
class BillingAccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BillingAccountRepository billingAccountRepository;

    private BillingAccount billingAccount1;
    private BillingAccount billingAccount2;
    private BillingAccount billingAccount3;
    private AcademyId academyId1;
    private AcademyId academyId2;

    @BeforeEach
    void setUp() {
        // Arrange – datos comunes
        academyId1 = new AcademyId(1L);
        academyId2 = new AcademyId(2L);

        CreateBillingAccountCommand command1 = new CreateBillingAccountCommand(
                new StudentId(1L),
                new DniNumber("12345678")
        );
        billingAccount1 = new BillingAccount(command1, academyId1);

        CreateBillingAccountCommand command2 = new CreateBillingAccountCommand(
                new StudentId(2L),
                new DniNumber("87654321")
        );
        billingAccount2 = new BillingAccount(command2, academyId1);

        CreateBillingAccountCommand command3 = new CreateBillingAccountCommand(
                new StudentId(3L),
                new DniNumber("11223344")
        );
        billingAccount3 = new BillingAccount(command3, academyId2);
    }

    @Test
    @DisplayName("Should persist and retrieve BillingAccount with embedded value objects")
    void save_ValidBillingAccount_PersistsSuccessfully() {
        // Arrange – billingAccount1 ya está construido en setUp()

        // Act
        BillingAccount saved = billingAccountRepository.save(billingAccount1);
        entityManager.flush();
        entityManager.clear();

        BillingAccount found = entityManager.find(BillingAccount.class, saved.getId());

        // Assert
        assertNotNull(found);
        assertNotNull(found.getId(), "El ID debe ser generado al persistir");
        assertEquals(1L, found.getStudentId().studentId());
        assertEquals("12345678", found.getDniNumber().dniNumber());
        assertEquals(academyId1.academyId(), found.getAcademyId().academyId());
    }

    @Test
    @DisplayName("Should find BillingAccount by StudentId")
    void findByStudentId_ExistingStudent_ReturnsBillingAccount() {
        // Arrange
        entityManager.persist(billingAccount1);
        entityManager.flush();

        StudentId studentIdToFind = new StudentId(1L);

        // Act
        Optional<BillingAccount> result = billingAccountRepository.findByStudentId(studentIdToFind);

        // Assert
        assertTrue(result.isPresent());
        BillingAccount found = result.get();
        assertEquals(1L, found.getStudentId().studentId());
        assertEquals("12345678", found.getDniNumber().dniNumber());
        assertEquals(academyId1.academyId(), found.getAcademyId().academyId());
    }

    @Test
    @DisplayName("Should return empty Optional when BillingAccount not found by StudentId")
    void findByStudentId_NonExistingStudent_ReturnsEmpty() {
        // Arrange
        entityManager.persist(billingAccount1);
        entityManager.flush();

        StudentId nonExistingStudentId = new StudentId(999L);

        // Act
        Optional<BillingAccount> result = billingAccountRepository.findByStudentId(nonExistingStudentId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find all BillingAccounts by AcademyId")
    void findAllByAcademyId_ExistingAcademy_ReturnsAccountList() {
        // Arrange
        entityManager.persist(billingAccount1);
        entityManager.persist(billingAccount2);
        entityManager.persist(billingAccount3);
        entityManager.flush();

        // Act
        List<BillingAccount> accountsInAcademy1 = billingAccountRepository.findAllByAcademyId(academyId1);

        // Assert
        assertEquals(2, accountsInAcademy1.size());
        assertTrue(accountsInAcademy1.stream()
                .allMatch(a -> a.getAcademyId().academyId().equals(academyId1.academyId())));
        assertTrue(accountsInAcademy1.stream()
                .anyMatch(a -> a.getStudentId().studentId().equals(1L)));
        assertTrue(accountsInAcademy1.stream()
                .anyMatch(a -> a.getStudentId().studentId().equals(2L)));
    }

    @Test
    @DisplayName("Should return empty list when no BillingAccounts exist for AcademyId")
    void findAllByAcademyId_NonExistingAcademy_ReturnsEmptyList() {
        // Arrange
        entityManager.persist(billingAccount1);
        entityManager.flush();

        AcademyId nonExistingAcademy = new AcademyId(999L);

        // Act
        List<BillingAccount> result = billingAccountRepository.findAllByAcademyId(nonExistingAcademy);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should maintain data integrity with multiple BillingAccounts")
    void multipleOperations_MaintainsIntegrity() {
        // Arrange
        entityManager.persist(billingAccount1);
        entityManager.persist(billingAccount2);
        entityManager.persist(billingAccount3);
        entityManager.flush();

        // Act
        List<BillingAccount> allAccounts = billingAccountRepository.findAll();
        List<BillingAccount> academy1Accounts = billingAccountRepository.findAllByAcademyId(academyId1);
        List<BillingAccount> academy2Accounts = billingAccountRepository.findAllByAcademyId(academyId2);

        // Assert
        assertEquals(3, allAccounts.size());
        assertEquals(2, academy1Accounts.size());
        assertEquals(1, academy2Accounts.size());
    }
}