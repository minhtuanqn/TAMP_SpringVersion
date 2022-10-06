package com.tamp_backend.controller;

import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.partner.CreatePartnerModel;
import com.tamp_backend.model.partner.PartnerFilterModel;
import com.tamp_backend.model.partner.PartnerModel;
import com.tamp_backend.model.partner.UpdatePartnerModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.PartnerService;
import com.tamp_backend.service.SystemAdminService;
import com.tamp_backend.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/partners")
public class PartnerController {
    private PartnerService partnerService;
    private SystemAdminService systemAdminService;

    public PartnerController(PartnerService partnerService, SystemAdminService systemAdminService) {
        this.partnerService = partnerService;
        this.systemAdminService = systemAdminService;
    }

    /**
     * Create new partner
     *
     * @param requestModel
     * @param logo
     * @return response model contains created partner model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> createPartner(@Valid @ModelAttribute CreatePartnerModel requestModel, @RequestPart MultipartFile logo) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        PartnerModel savedPartnerModel = partnerService.createPartner(requestModel, "", curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(savedPartnerModel).message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete a partner model
     *
     * @param id
     * @return response model contains deleted partner model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deletePartner(@PathVariable UUID id) {
        PartnerModel deletedPartnerModel = partnerService.deletePartner(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(deletedPartnerModel).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find partner by id
     *
     * @param id
     * @return response model contains partner model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> findPartnerById(@PathVariable UUID id) {
        PartnerModel partnerModel = partnerService.findPartnerById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(partnerModel).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete partners by list of id
     *
     * @param ids
     * @return response model contains deleted partners
     */
    @DeleteMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deletePartners(@RequestBody List<UUID> ids) {
        List<PartnerModel> partnerModels = partnerService.deletePartnersByIds(ids);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(partnerModels).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search partners by name
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource data of partner
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> searchPartners(@RequestParam(value = "searchedValue", defaultValue = "") String searchedValue, @RequestPagingParam PaginationRequestModel paginationRequestModel, @ModelAttribute PartnerFilterModel partnerFilterModel) {
        ResourceModel<PartnerModel> partnerList = partnerService.searchPartners(searchedValue, paginationRequestModel, partnerFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(partnerList).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update partner
     *
     * @param requestModel
     * @param logo
     * @return updated partner model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updatePartner(@Valid @ModelAttribute UpdatePartnerModel requestModel, @RequestPart MultipartFile logo) {
        PartnerModel updatedModel = partnerService.updatePartner(requestModel, null);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
