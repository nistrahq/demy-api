package com.nistra.demy.platform.institution.interfaces.rest.controllers;

import com.nistra.demy.platform.institution.domain.services.AdministratorCommandService;
import com.nistra.demy.platform.institution.interfaces.rest.resources.RegisterAdministratorResource;
import com.nistra.demy.platform.institution.interfaces.rest.transform.AdministratorResourceFromEntityAssembler;
import com.nistra.demy.platform.institution.interfaces.rest.transform.RegisterAdministratorCommandFromResourceAssembler;
import com.nistra.demy.platform.shared.interfaces.rest.resources.MessageResource;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/v1/administrators", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Administrators", description = "Endpoints for managing administrators")
public class AdministratorsController {

    private final AdministratorCommandService administratorCommandService;

    public AdministratorsController(AdministratorCommandService administratorCommandService) {
        this.administratorCommandService = administratorCommandService;
    }

    @PostMapping
    public ResponseEntity<MessageResource> registerAdministrator(@RequestBody RegisterAdministratorResource resource) {
        var registerAdministratorCommand = RegisterAdministratorCommandFromResourceAssembler.toCommandFromResource(resource);
        var administrator = administratorCommandService.handle(registerAdministratorCommand);
        if (administrator.isEmpty()) return ResponseEntity.badRequest().build();
        var administratorEntity = administrator.get();
        var administratorResource = AdministratorResourceFromEntityAssembler.toResourceFromEntity(administratorEntity);
        return ResponseEntity.ok(new MessageResource("Administrator registered successfully"));
    }
}
