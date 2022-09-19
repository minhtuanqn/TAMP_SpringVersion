package com.tamp_backend.controller;

import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.systemadmin.SystemAdminModel;
import com.tamp_backend.service.SystemAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/system-admins")
public class SystemAdminController {

    private SystemAdminService systemAdminService;

    public SystemAdminController(SystemAdminService systemAdminService) {
        this.systemAdminService = systemAdminService;
    }

    /**
     * Find system admin by id
     * @param id
     * @return response model contains supplier model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> findSystemAdminById(@PathVariable UUID id) {
        SystemAdminModel systemAdminModel = systemAdminService.findSystemAdminById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(systemAdminModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
