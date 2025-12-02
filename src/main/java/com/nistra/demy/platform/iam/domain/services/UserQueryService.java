package com.nistra.demy.platform.iam.domain.services;

import com.nistra.demy.platform.iam.domain.model.queries.GetAuthenticatedUserTenantIdQuery;
import com.nistra.demy.platform.iam.domain.model.valueobjects.TenantId;

import java.util.Optional;

public interface UserQueryService {
    Optional<TenantId> handle(GetAuthenticatedUserTenantIdQuery query);
}
