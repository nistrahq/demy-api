package com.nistra.demy.platform.enrollment.application.acl;

import com.nistra.demy.platform.enrollment.domain.model.queries.GetStudentByDniQuery;
import com.nistra.demy.platform.enrollment.domain.services.StudentQueryService;
import com.nistra.demy.platform.enrollment.interfaces.acl.EnrollmentsContextFacade;
import com.nistra.demy.platform.shared.domain.model.valueobjects.DniNumber;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentsContextFacadeImpl implements EnrollmentsContextFacade {
    private final StudentQueryService studentQueryService;

    public EnrollmentsContextFacadeImpl(StudentQueryService studentQueryService) {
        this.studentQueryService = studentQueryService;
    }

    public String fetchStudentFullNameByDni(String dni) {
        var getStudentByDniQuery = new GetStudentByDniQuery(new DniNumber(dni));
        var student = studentQueryService.handle(getStudentByDniQuery);
        return String.valueOf(student.map(value -> value.getPersonName().firstName()).orElse(""));
    }
}
