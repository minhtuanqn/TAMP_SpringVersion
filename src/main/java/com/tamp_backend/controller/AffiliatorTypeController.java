package com.tamp_backend.controller;

import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.affiliatortype.AffiliatorTypeFilterModel;
import com.tamp_backend.model.affiliatortype.AffiliatorTypeModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.affiliatortype.CreateAffiliatorTypeModel;
import com.tamp_backend.model.affiliatortype.UpdateAffiliatorTypeModel;
import com.tamp_backend.model.partnertype.PartnerTypeModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.AffiliatorTypeService;
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
@RequestMapping(path = "/affiliator-types")
public class AffiliatorTypeController {
    private AffiliatorTypeService affiliatorTypeService;

    public AffiliatorTypeController(AffiliatorTypeService affiliatorTypeService) {
        this.affiliatorTypeService = affiliatorTypeService;
    }

    /**
     * Search affiliaror type by name
     * @param searchText
     * @param paginationRequestModel
     * @param affiliatorTypeFilterModel
     * @return resource data of affiliaror type
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> searchAffiliatorType(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                       @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                       @ModelAttribute AffiliatorTypeFilterModel affiliatorTypeFilterModel) {
        ResourceModel<AffiliatorTypeModel> affiliatorTypeList = affiliatorTypeService
                .searchAffiliatorTypes(searchText, paginationRequestModel, affiliatorTypeFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(affiliatorTypeList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Create new affiliaror type
     * @param requestModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> createAffilatorType(@Valid @RequestBody CreateAffiliatorTypeModel requestModel) {
        AffiliatorTypeModel savedModel = affiliatorTypeService.createAffiliatorType(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update affiliaror type
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateAffiliatorType(@Valid @RequestBody UpdateAffiliatorTypeModel requestModel) {
        AffiliatorTypeModel updatedModel = affiliatorTypeService.updateAffType(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find affiliaror type by id
     *
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> findAffiliatorTypeById(@PathVariable UUID id) {
        AffiliatorTypeModel affiliatorTypeModel = affiliatorTypeService.findAffiliatorTypeById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(affiliatorTypeModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * delete affiliaror type
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteAffiliatorType(@PathVariable UUID id){
        AffiliatorTypeModel deletedModel = affiliatorTypeService.deleteAffiliatorType(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete affiliator type list by ids
     * @param ids
     * @return deleted affiliator types list
     */
    @DeleteMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteAffiliatorTypes(@RequestBody List<UUID> ids) {
        List<AffiliatorTypeModel> deletedModels = affiliatorTypeService.deleteAffiliatortypes(ids);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModels)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
