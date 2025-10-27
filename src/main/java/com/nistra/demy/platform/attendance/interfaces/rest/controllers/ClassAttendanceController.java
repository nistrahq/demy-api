package com.nistra.demy.platform.attendance.interfaces.rest.controllers;


import com.nistra.demy.platform.attendance.domain.model.aggregates.ClassAttendance;
import com.nistra.demy.platform.attendance.domain.model.commands.UpdateAttendanceRecordStatusCommand;
import com.nistra.demy.platform.attendance.domain.model.queries.GetAllClassAttendancesByAcademyQuery;
import com.nistra.demy.platform.attendance.domain.model.queries.GetClassAttendanceByIdQuery;
import com.nistra.demy.platform.attendance.domain.model.valueobjects.AttendanceStatus;
import com.nistra.demy.platform.attendance.domain.services.ClassAttendanceCommandService;
import com.nistra.demy.platform.attendance.domain.services.ClassAttendanceQueryService;
import com.nistra.demy.platform.attendance.interfaces.rest.resources.ClassAttendanceResource;
import com.nistra.demy.platform.attendance.interfaces.rest.resources.CreateClassAttendanceResource;
import com.nistra.demy.platform.attendance.interfaces.rest.transform.ClassAttendanceResourceFromEntityAssembler;
import com.nistra.demy.platform.attendance.interfaces.rest.transform.CreateClassAttendanceFromResourceAssembler;
import com.nistra.demy.platform.shared.domain.model.valueobjects.DniNumber;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    private final ClassAttendanceQueryService classAttendanceQueryService;


    public ClassAttendanceController(ClassAttendanceCommandService classAttendanceCommandService,
                                     ClassAttendanceQueryService classAttendanceQueryService) {
        this.classAttendanceCommandService = classAttendanceCommandService;
        this.classAttendanceQueryService = classAttendanceQueryService;
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

    @Operation(
            summary = "Update attendance status for a student (by DNI)",
            description = "Actualiza el estado de asistencia (PRESENT, ABSENT, EXCUSED) de un estudiante dentro de un ClassAttendance. " +
                    "Alcance por Academia (tenant)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance status actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Bad Request (estado inválido o formato incorrecto)"),
            @ApiResponse(responseCode = "404", description = "ClassAttendance o registro de DNI no encontrado")
    })
    @PatchMapping("/{id}/attendance/{dni}/status")
    public ResponseEntity<?> patchStatus(@PathVariable Long id,
                                         @PathVariable String dni,
                                         @RequestBody UpdateAttendanceStatusResource body) {
        var command = new UpdateAttendanceRecordStatusCommand(
                id, new DniNumber(dni), body.status()
        );
        var updated = classAttendanceCommandService.handle(command);

        return updated
                .map(agg -> ResponseEntity.ok(
                        ClassAttendanceResourceFromEntityAssembler.toResourceFromEntity(agg)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record UpdateAttendanceStatusResource(AttendanceStatus status) {}


    @Operation(
            summary = "List all class attendances (scoped by Academy)",
            description = "Lista todas las asistencias de clase de la Academia actual (tenant)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ClassAttendanceResource>> listAllByAcademy() {
        var list = classAttendanceQueryService.handle(new GetAllClassAttendancesByAcademyQuery());
        var resources = list.stream()
                .map(ClassAttendanceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    @Operation(summary = "Get a class attendance by id (scoped by Academy)",
            description = "Obtiene un ClassAttendance por su ID dentro del alcance de la Academia actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClassAttendanceResource> getById(@PathVariable Long id) {
        var maybe = classAttendanceQueryService.handle(new GetClassAttendanceByIdQuery(id));
        return maybe
                .map(agg -> ResponseEntity.ok(ClassAttendanceResourceFromEntityAssembler.toResourceFromEntity(agg)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

