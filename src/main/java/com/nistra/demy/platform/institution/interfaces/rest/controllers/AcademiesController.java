package com.nistra.demy.platform.institution.interfaces.rest.controllers;

import com.nistra.demy.platform.institution.domain.model.queries.ExistsAcademyByIdQuery;
import com.nistra.demy.platform.institution.domain.model.queries.GetAcademyByIdQuery;
import com.nistra.demy.platform.institution.domain.services.AcademyCommandService;
import com.nistra.demy.platform.institution.domain.services.AcademyQueryService;
import com.nistra.demy.platform.institution.interfaces.rest.resources.AcademyResource;
import com.nistra.demy.platform.institution.interfaces.rest.resources.RegisterAcademyResource;
import com.nistra.demy.platform.institution.interfaces.rest.transform.AcademyResourceFromEntityAssembler;
import com.nistra.demy.platform.institution.interfaces.rest.transform.RegisterAcademyCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/academies", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Academies", description = "Endpoints for managing academies")
public class AcademiesController {

    private final AcademyCommandService academyCommandService;
    private final AcademyQueryService academyQueryService;

    public AcademiesController(
            AcademyCommandService academyCommandService,
            AcademyQueryService academyQueryService
    ) {
        this.academyCommandService = academyCommandService;
        this.academyQueryService = academyQueryService;
    }

    @PostMapping
    public ResponseEntity<AcademyResource> registerAcademy(@RequestBody RegisterAcademyResource resource) {
        var registerAcademyCommand = RegisterAcademyCommandFromResourceAssembler.toCommandFromResource(resource);
        var academy = academyCommandService.handle(registerAcademyCommand);
        if (academy.isEmpty()) return ResponseEntity.badRequest().build();
        var academyEntity = academy.get();
        var academyResource = AcademyResourceFromEntityAssembler.toResourceFromEntity(academyEntity);
        return new ResponseEntity<>(academyResource, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkAcademyExists(@PathVariable Long id) {
        boolean exists = academyQueryService.handle(new ExistsAcademyByIdQuery(id));
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
