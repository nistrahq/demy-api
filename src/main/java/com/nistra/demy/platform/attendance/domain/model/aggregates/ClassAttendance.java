package com.nistra.demy.platform.attendance.domain.model.aggregates;


import com.nistra.demy.platform.attendance.domain.model.commands.CreateClassAttendanceCommand;
import com.nistra.demy.platform.attendance.domain.model.entities.AttendanceRecord;
import com.nistra.demy.platform.attendance.domain.model.valueobjects.ClassSessionId;
import com.nistra.demy.platform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.nistra.demy.platform.shared.domain.model.valueobjects.AcademyId;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ClassAttendance aggregate root
 * @author diego
 *
 * @summary
 * The ClassAttendance class is an aggregate root that represents a class attendance
 * It is responsible for handling the CreateClassAttendanceCommand command
 */
@Entity
public class ClassAttendance extends AuditableAbstractAggregateRoot<ClassAttendance> {


    @Embedded
    @Getter
    private ClassSessionId classSessionId;

    @Column(nullable = false)
    @Getter
    private LocalDate date;


    @OneToMany(mappedBy = "classAttendance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<AttendanceRecord> attendance = new ArrayList<>();

    @Embedded
    @Getter
    private AcademyId academyId;


    /**
     * Required by JPA
     */

    protected ClassAttendance() {}


    public ClassAttendance(AcademyId academyId, CreateClassAttendanceCommand command) {
        this.academyId = Objects.requireNonNull(academyId, "academyId is required");
        this.classSessionId = command.classSessionId();
        this.date = command.date();
        command.attendance().forEach(input -> this.addAttendance(
                new AttendanceRecord(this, input.dni(), input.status())
        ));
    }


    public void addAttendance(AttendanceRecord record) {
        this.attendance.add(record);
    }
}
