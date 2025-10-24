package com.nistra.demy.platform.iam.interfaces.acl;

import java.util.List;

public interface IamContextFacade {
    Long signUpVerifiedUser(String email, List<String> roles);

    Long fetchAuthenticatedUserTenantId();
}
