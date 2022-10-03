package com.tamp_backend.controller;


import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.partnertype.CreatePartnerTypeModel;
import com.tamp_backend.model.partnertype.PartnerTypeFilterModel;
import com.tamp_backend.model.partnertype.PartnerTypeModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.partnertype.UpdatePartnerTypeModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.PartnerTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/partner-types")
public class PartnerTypeController {
    private PartnerTypeService partnerTypeService;

    public PartnerTypeController(PartnerTypeService partnerTypeService) {
        this.partnerTypeService = partnerTypeService;
    }

    /**
     * Search partner type by name
     * @param searchText
     * @param paginationRequestModel
     * @param partnerTypeFilterModel
     * @return resource data of partner type
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER')")
    public ResponseEntity<ResponseModel> searchPartnerType(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                           @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                           @ModelAttribute PartnerTypeFilterModel partnerTypeFilterModel) {
        ResourceModel<PartnerTypeModel> partnerTypeList = partnerTypeService
                .searchPartnerTypes(searchText, paginationRequestModel, partnerTypeFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(partnerTypeList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Create new partner type
     * @param requestModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> createPartnerType(@Valid @RequestBody CreatePartnerTypeModel requestModel) {
        PartnerTypeModel savedModel = partnerTypeService.createPartnerType(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update partner type
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updatePartnerType(@Valid @RequestBody UpdatePartnerTypeModel requestModel) {
        PartnerTypeModel updatedModel = partnerTypeService.updatePartnerType(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find partner type by id
     *
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER')")
    public ResponseEntity<ResponseModel> findPartnerTypeById(@PathVariable UUID id) {
        PartnerTypeModel partnerTypeModel = partnerTypeService.findPartnerTypeById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(partnerTypeModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * delete partner type
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deletePartnerType(@PathVariable UUID id){
        PartnerTypeModel deletedModel = partnerTypeService.deletePartnerType(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete partner type list by ids
     * @param ids
     * @return deleted partner types list
     */
    @DeleteMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deletePartnerTypes(@RequestBody List<UUID> ids) {
        List<PartnerTypeModel> deletedModels = partnerTypeService.deleteParnertypes(ids);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModels)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
