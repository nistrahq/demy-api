package com.nistra.demy.platform.iam.application.acl;

import com.nistra.demy.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.nistra.demy.platform.iam.application.internal.outboundservices.verification.VerificationService;
import com.nistra.demy.platform.iam.domain.model.commands.SignUpCommand;
import com.nistra.demy.platform.iam.domain.model.commands.SignUpVerifiedUserCommand;
import com.nistra.demy.platform.iam.domain.model.entities.Role;
import com.nistra.demy.platform.iam.domain.model.queries.GetAuthenticatedUserTenantIdQuery;
import com.nistra.demy.platform.iam.domain.services.UserCommandService;
import com.nistra.demy.platform.iam.domain.services.UserQueryService;
import com.nistra.demy.platform.iam.interfaces.acl.IamContextFacade;
import com.nistra.demy.platform.shared.domain.model.valueobjects.EmailAddress;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final VerificationService verificationService;

    public IamContextFacadeImpl(
            UserQueryService userQueryService,
            UserCommandService userCommandService,
            VerificationService verificationService
    ) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
        this.verificationService = verificationService;
    }

    @Override
    public Long signUpVerifiedUser(String email, List<String> roles) {
        var randomPassword = verificationService.generateRandomPassword();
        var signUpUserCommand = new SignUpVerifiedUserCommand(
                new EmailAddress(email),
                randomPassword,
                roles.stream().map(Role::toRoleFromName).toList());
        var signedUpUser = userCommandService.handle(signUpUserCommand)
                .orElseThrow(() -> new IllegalStateException("User sign up failed"));
        return signedUpUser.getId();
    }

    @Override
    public Long fetchAuthenticatedUserTenantId() {
        var getAuthenticatedUserTenantIdQuery = new GetAuthenticatedUserTenantIdQuery();
        var tenantId = userQueryService.handle(getAuthenticatedUserTenantIdQuery);
        return tenantId.isEmpty() ? Long.valueOf(0L) : tenantId.get().tenantId();
    }
}
