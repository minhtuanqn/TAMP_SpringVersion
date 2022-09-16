package com.tamp_backend.controller;

import com.tamp_backend.model.affiliatortype.AffiliatorTypeModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.AffiliatorTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/affiliator-types")
public class AffiliatorTypeController {
    private AffiliatorTypeService affiliatorTypeService;

    public AffiliatorTypeController(AffiliatorTypeService affiliatorTypeService) {
        this.affiliatorTypeService = affiliatorTypeService;
    }

    /**
     * Search affiliaror type by name
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource data of affiliaror type
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<Object> searchAffiliatorType(@RequestParam(value = "searchedValue", defaultValue = "") String searchedValue,
                                                    @RequestPagingParam PaginationRequestModel paginationRequestModel) {
        ResourceModel<AffiliatorTypeModel> affiliatorTypeList = affiliatorTypeService.searchAffiliatorTypes(searchedValue, paginationRequestModel);
        return new ResponseEntity<>(affiliatorTypeList, HttpStatus.OK);
    }

    /**
     * Create new affiliaror type
     *
     * @param requestModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<AffiliatorTypeModel> createAffilatorType(@Valid @RequestBody AffiliatorTypeModel requestModel) {
        AffiliatorTypeModel savedModel = affiliatorTypeService.createAffiliatorType(requestModel);
        return new ResponseEntity<>(savedModel, HttpStatus.OK);
    }

    /**
     * Update affiliaror type
     *
     * @param id
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<AffiliatorTypeModel> updateAffiliatorType(@PathVariable UUID id,
                                                              @Valid @RequestBody AffiliatorTypeModel requestModel) {
        AffiliatorTypeModel updatedModel = affiliatorTypeService.updateAffType(id, requestModel);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    /**
     * Find affiliaror type by id
     *
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<AffiliatorTypeModel> findAffiliatorTypeById(@PathVariable UUID id) {
        AffiliatorTypeModel model = affiliatorTypeService.findAffiliatorTypeById(id);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    /**
     * delete affiliaror type
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<AffiliatorTypeModel> deleteAffiliatorType(@PathVariable UUID id){
        AffiliatorTypeModel deletedModel = affiliatorTypeService.deleteAffiliatorType(id);
        return new ResponseEntity<>(deletedModel, HttpStatus.OK);
    }

}
