package com.nistra.demy.platform.institution.application.internal.queryservices;

import com.nistra.demy.platform.institution.domain.model.aggregates.Academy;
import com.nistra.demy.platform.institution.domain.model.queries.GetAcademyByIdQuery;
import com.nistra.demy.platform.institution.domain.services.AcademyQueryService;
import com.nistra.demy.platform.institution.infrastructure.persistence.jpa.repositories.AcademyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AcademyQueryServiceImpl implements AcademyQueryService {

    private final AcademyRepository academyRepository;

    public AcademyQueryServiceImpl(AcademyRepository academyRepository) {
        this.academyRepository = academyRepository;
    }

    @Override
    public Optional<Academy> handle(GetAcademyByIdQuery query) {
        return academyRepository.findById(query.academyId());
    }
}
