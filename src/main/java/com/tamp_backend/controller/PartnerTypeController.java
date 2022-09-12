package com.tamp_backend.controller;


import com.tamp_backend.entity.PartnerTypeEntity;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.PartnerTypeModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.PartnerTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
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
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource data of partner type
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> searchPartnerType(@RequestParam(value = "searchedValue", defaultValue = "") String searchedValue,
                                                    @RequestPagingParam PaginationRequestModel paginationRequestModel) {
        ResourceModel<PartnerTypeModel> partnerTypeList = partnerTypeService.searchPartnerTypes(searchedValue, paginationRequestModel);
        return new ResponseEntity<>(partnerTypeList, HttpStatus.OK);
    }

    /**
     * Create new partner type
     *
     * @param requestModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PartnerTypeModel> createPartnerType(@Valid @RequestBody PartnerTypeModel requestModel) {
        PartnerTypeModel savedModel = partnerTypeService.createPartnerType(requestModel);
        return new ResponseEntity<>(savedModel, HttpStatus.OK);
    }

    /**
     * Update partner type
     *
     * @param id
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PartnerTypeModel> updatePartnerType(@PathVariable UUID id,
                                                              @Valid @RequestBody PartnerTypeModel requestModel) {
        PartnerTypeModel updatedModel = partnerTypeService.updatePartnerType(id, requestModel);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    /**
     * Find partner type by id
     *
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PartnerTypeModel> findPartnerTypeById(@PathVariable UUID id) {
        PartnerTypeModel model = partnerTypeService.findPartnerTypeById(id);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    /**
     * delete partner type
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<PartnerTypeModel> deletePartnerType(@PathVariable UUID id){
        PartnerTypeModel deletedModel = partnerTypeService.deletePartnerType(id);
        return new ResponseEntity<>(deletedModel, HttpStatus.OK);
    }



}
