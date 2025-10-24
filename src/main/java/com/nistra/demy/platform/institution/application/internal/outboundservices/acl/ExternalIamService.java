package com.nistra.demy.platform.institution.application.internal.outboundservices.acl;

import com.nistra.demy.platform.iam.interfaces.acl.IamContextFacade;
import com.nistra.demy.platform.institution.domain.model.valueobjects.InstitutionRoles;
import com.nistra.demy.platform.institution.domain.model.valueobjects.UserId;
import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public Optional<AcademyId> fetchCurrentAcademyId() {
        var academyId = iamContextFacade.fetchAuthenticatedUserTenantId();
        return academyId == 0L ? Optional.empty() : Optional.of(new AcademyId(academyId));
    }

    public Optional<UserId> registerTeacher(String email) {
        var userId = iamContextFacade.signUpVerifiedUser(email, List.of(InstitutionRoles.ROLE_TEACHER.name()));
        return userId == 0L ? Optional.empty() : Optional.of(new UserId(userId));
    }
}
