package com.nistra.demy.platform.enrollment.application.internal.commandservices;

import com.nistra.demy.platform.enrollment.application.internal.outboundservices.acl.ExternalIamService;
import com.nistra.demy.platform.enrollment.domain.model.aggregates.Student;
import com.nistra.demy.platform.enrollment.domain.model.commands.CreateStudentCommand;
import com.nistra.demy.platform.enrollment.domain.model.commands.DeleteStudentCommand;
import com.nistra.demy.platform.enrollment.domain.model.commands.UpdateStudentCommand;
import com.nistra.demy.platform.enrollment.domain.services.StudentCommandService;
import com.nistra.demy.platform.enrollment.infrastructure.persistence.jpa.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentCommandServiceImpl implements StudentCommandService {

    private final StudentRepository studentRepository;
    private final ExternalIamService externalIamService;

    public StudentCommandServiceImpl(
            ExternalIamService externalIamService,
            StudentRepository studentRepository) {
        this.externalIamService = externalIamService;
        this.studentRepository = studentRepository;
    }

    @Override
    public Long handle(CreateStudentCommand command) {
        if (studentRepository.existsStudentByDni(command.dni())) {
            throw new IllegalArgumentException("Student with DNI %s already exists".formatted(command.dni()));
        }

        var userId = externalIamService.registerStudent(command.emailAddress().email())
                .orElseThrow(() -> new IllegalArgumentException("Email address already in use"));

        var academyId = externalIamService.fetchCurrentAcademyId()
                .orElseThrow(() -> new IllegalArgumentException("Academy not found"));

        var student = new Student(
                command.personName(),
                command.dni(),
                command.sex(),
                command.birthDate(),
                command.streetAddress(),
                command.phoneNumber(),
                academyId,
                userId
        );

        try {
            studentRepository.save(student);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create student: " + e.getMessage(), e);
        }

        return student.getId();
    }

    @Override
    public void handle(DeleteStudentCommand command) {
        if (!studentRepository.existsById(command.studentId())) {
            throw new IllegalArgumentException("Student with ID %s does not exist".formatted(command.studentId()));
        }

        try {
            studentRepository.deleteById(command.studentId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error while deleting student");
        }
    }

    @Override
    public Optional<Student> handle(UpdateStudentCommand command) {
        var student = studentRepository.findById(command.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student with ID %s does not exist".formatted(command.studentId())));

        student.updateInformation(
                command.personName(),
                command.dniNumber(),
                command.sex(),
                command.birthDate(),
                command.streetAddress(),
                command.phoneNumber()
        );

        try {
            studentRepository.save(student);
            return Optional.of(student);
        }  catch (Exception e) {
            throw new IllegalArgumentException("Error while updating student");
        }
    }

}
