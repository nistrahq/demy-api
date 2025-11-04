package com.nistra.demy.platform.enrollment.domain.services;

import com.nistra.demy.platform.enrollment.domain.model.aggregates.Student;
import com.nistra.demy.platform.enrollment.domain.model.queries.*;
import com.nistra.demy.platform.shared.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

public interface StudentQueryService {
    Optional<Student> handle(GetStudentByIdQuery query);
    List<Student> handle(GetAllStudentsQuery query);
    Optional<Student> handle(GetStudentByDniQuery query);
    Optional<EmailAddress> handle(GetStudentEmailAddressByUserIdQuery query);
}
