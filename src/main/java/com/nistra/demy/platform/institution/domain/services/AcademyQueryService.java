package com.nistra.demy.platform.institution.domain.services;

import com.nistra.demy.platform.institution.domain.model.aggregates.Academy;
import com.nistra.demy.platform.institution.domain.model.queries.GetAcademyByIdQuery;

import java.util.Optional;

public interface AcademyQueryService {

    Optional<Academy> handle(GetAcademyByIdQuery query);
}
