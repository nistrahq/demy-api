package com.nistra.demy.platform.iam.application.acl;

import com.nistra.demy.platform.iam.domain.model.queries.GetAuthenticatedUserTenantIdQuery;
import com.nistra.demy.platform.iam.domain.services.UserQueryService;
import com.nistra.demy.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

@Service
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserQueryService userQueryService;

    public IamContextFacadeImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public Long fetchAuthenticatedUserTenantId() {
        var getAuthenticatedUserTenantIdQuery = new GetAuthenticatedUserTenantIdQuery();
        var tenantId = userQueryService.handle(getAuthenticatedUserTenantIdQuery);
        return tenantId.isEmpty() ? Long.valueOf(0L) : tenantId.get().tenantId();
    }
}
