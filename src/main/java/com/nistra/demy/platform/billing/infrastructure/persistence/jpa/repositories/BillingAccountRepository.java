package com.nistra.demy.platform.billing.infrastructure.persistence.jpa.repositories;

import com.nistra.demy.platform.billing.domain.model.aggregates.BillingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingAccountRepository extends JpaRepository<BillingAccount, Long> {
}
