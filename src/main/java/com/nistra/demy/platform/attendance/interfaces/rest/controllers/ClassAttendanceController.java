package com.nistra.demy.platform.attendance.interfaces.rest.controllers;


import com.nistra.demy.platform.attendance.domain.model.aggregates.ClassAttendance;
import com.nistra.demy.platform.attendance.domain.services.ClassAttendanceCommandService;
import com.nistra.demy.platform.attendance.interfaces.rest.resources.ClassAttendanceResource;
import com.nistra.demy.platform.attendance.interfaces.rest.resources.CreateClassAttendanceResource;
import com.nistra.demy.platform.attendance.interfaces.rest.transform.ClassAttendanceResourceFromEntityAssembler;
import com.nistra.demy.platform.attendance.interfaces.rest.transform.CreateClassAttendanceFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


/**
 * REST controller for class attendance sources.
 * @summary
 * This class provides REST endpoints for class attendances.
 */
@RestController
@RequestMapping(value="/api/v1/class-attendances", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Class Attendances", description = "Endpoints for Attendance Bounded Context")
public class ClassAttendanceController {
    private final ClassAttendanceCommandService classAttendanceCommandService;


    public ClassAttendanceController(ClassAttendanceCommandService classAttendanceCommandService) {
        this.classAttendanceCommandService = classAttendanceCommandService;
    }

    @Operation(
            summary = "Creates a class attendance",
            description="Creates a class attendance with the list of attendance records, classSessionId and date"
    )
    @ApiResponses( value={
            @ApiResponse(responseCode = "201", description = "Class attendance created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<ClassAttendanceResource> createClassAttendance(@RequestBody CreateClassAttendanceResource resource){
        Optional<ClassAttendance> classAttendance = classAttendanceCommandService.
                handle(CreateClassAttendanceFromResourceAssembler.toCommandFromResource(resource));
        return classAttendance.map( source ->
                new ResponseEntity<>(ClassAttendanceResourceFromEntityAssembler.toResourceFromEntity(source), CREATED))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}

