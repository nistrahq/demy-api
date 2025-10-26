package com.nistra.demy.platform.institution.domain.services;

import com.nistra.demy.platform.institution.domain.model.aggregates.Teacher;
import com.nistra.demy.platform.institution.domain.model.queries.GetAllTeachersQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetTeacherEmailAddressByUserIdQuery;
import com.nistra.demy.platform.shared.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

public interface TeacherQueryService {

    List<Teacher> handle(GetAllTeachersQuery query);

    Optional<EmailAddress> handle(GetTeacherEmailAddressByUserIdQuery query);
}
