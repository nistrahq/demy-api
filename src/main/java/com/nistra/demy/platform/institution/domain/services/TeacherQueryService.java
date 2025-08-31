package com.nistra.demy.platform.institution.domain.services;

import com.nistra.demy.platform.institution.domain.model.aggregates.Teacher;
import com.nistra.demy.platform.institution.domain.model.queries.GetAllTeachersQuery;

import java.util.List;

public interface TeacherQueryService {

    List<Teacher> handle(GetAllTeachersQuery query);
}
