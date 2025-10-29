package com.nistra.demy.platform.institution.domain.services;

import com.nistra.demy.platform.institution.domain.model.aggregates.Academy;
import com.nistra.demy.platform.institution.domain.model.aggregates.Teacher;
import com.nistra.demy.platform.institution.domain.model.queries.GetAcademyByIdQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetAllTeachersQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetTeacherByFullNameQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetTeacherByIdQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetTeacherEmailAddressByUserIdQuery;
import com.nistra.demy.platform.shared.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

public interface TeacherQueryService {

    List<Teacher> handle(GetAllTeachersQuery query);

    Optional<Teacher> handle(GetTeacherByFullNameQuery query);

    Optional<Teacher> handle(GetTeacherByIdQuery query);

    Optional<EmailAddress> handle(GetTeacherEmailAddressByUserIdQuery query);
}
