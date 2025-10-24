package com.nistra.demy.platform.institution.interfaces.rest.controllers;

import com.nistra.demy.platform.institution.domain.model.queries.GetAllTeachersQuery;
import com.nistra.demy.platform.institution.domain.services.TeacherCommandService;
import com.nistra.demy.platform.institution.domain.services.TeacherQueryService;
import com.nistra.demy.platform.institution.interfaces.rest.resources.RegisterTeacherResource;
import com.nistra.demy.platform.institution.interfaces.rest.resources.TeacherResource;
import com.nistra.demy.platform.institution.interfaces.rest.transform.RegisterTeacherCommandFromResourceAssembler;
import com.nistra.demy.platform.institution.interfaces.rest.transform.TeacherResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/teachers", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Teachers", description = "Endpoints for managing teachers")
public class TeachersController {

    private final TeacherCommandService teacherCommandService;
    private final TeacherQueryService teacherQueryService;

    public TeachersController(TeacherCommandService teacherCommandService, TeacherQueryService teacherQueryService) {
        this.teacherCommandService = teacherCommandService;
        this.teacherQueryService = teacherQueryService;
    }

    @PostMapping
    public ResponseEntity<TeacherResource> registerTeacher(@RequestBody RegisterTeacherResource resource) {
        var registerTeacherCommand = RegisterTeacherCommandFromResourceAssembler.toCommandFromResource(resource);
        var teacher = teacherCommandService.handle(registerTeacherCommand);
        if (teacher.isEmpty()) return ResponseEntity.badRequest().build();
        var teacherEntity = teacher.get();
        var teacherResource = TeacherResourceFromEntityAssembler.toResourceFromEntity(teacherEntity);
        return new ResponseEntity<>(teacherResource, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TeacherResource>> getAllTeachers() {
        var getAllTeachersQuery = new GetAllTeachersQuery();
        var teachers = teacherQueryService.handle(getAllTeachersQuery);
        var teacherResources = teachers.stream().map(TeacherResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(teacherResources);
    }
}
