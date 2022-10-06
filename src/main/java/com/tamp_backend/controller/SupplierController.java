package com.tamp_backend.controller;

import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.campaign.CampaignModel;
import com.tamp_backend.model.supplier.CreateSupplierModel;
import com.tamp_backend.model.supplier.SupplierFilterModel;
import com.tamp_backend.model.supplier.SupplierModel;
import com.tamp_backend.model.supplier.UpdateSupplierModel;
import com.tamp_backend.model.suppliercampaign.SupplierCampaignFilterModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.CampaignService;
import com.tamp_backend.service.SupplierService;
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
@RequestMapping(path = "/suppliers")
public class SupplierController {
    private SupplierService supplierService;
    private CampaignService campaignService;
    private SystemAdminService systemAdminService;

    public SupplierController(SupplierService supplierService, CampaignService campaignService, SystemAdminService systemAdminService) {
        this.supplierService = supplierService;
        this.campaignService = campaignService;
        this.systemAdminService = systemAdminService;
    }

    /**
     * Create new supplier
     * @param requestModel
     * @param logo
     * @return response model contains created supplier model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> createSupplier(@Valid @ModelAttribute CreateSupplierModel requestModel, @RequestPart MultipartFile logo) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        SupplierModel savedSupplierModel = supplierService.createSupplier(requestModel, "",curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedSupplierModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete a supplier model
     * @param id
     * @return response model contains deleted supplier model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteSupplier(@PathVariable UUID id) {
        SupplierModel deletedSupplierModel = supplierService.deleteSupplier(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedSupplierModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find supplier by id
     * @param id
     * @return response model contains supplier model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> findSupplierById(@PathVariable UUID id) {
        SupplierModel supplierModel = supplierService.findSupplierById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(supplierModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete suppliers by list of id
     * @param ids
     * @return response model contains deleted suppliers
     */
    @DeleteMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteSuppliers(@RequestBody List<UUID> ids) {
        List<SupplierModel> supplierModels = supplierService.deleteSuppliersByIds(ids);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(supplierModels)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update supplier
     * @param requestModel
     * @param logo
     * @return updated supplier model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateSupplier(@Valid @ModelAttribute UpdateSupplierModel requestModel, @RequestPart MultipartFile logo) {
        SupplierModel updatedModel = supplierService.updateSupplier(requestModel, null);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search suppliers by name
     * @param searchText
     * @param paginationRequestModel
     * @return resource data of supplier
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> searchSuppliers(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                   @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                   @ModelAttribute SupplierFilterModel supplierFilterModel) {
        ResourceModel<SupplierModel> supplierList = supplierService.searchSuppliers(searchText, paginationRequestModel, supplierFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(supplierList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search suppliers by name
     * @param searchText
     * @param paginationRequestModel
     * @return resource data of supplier
     */
    @GetMapping(path = "/{id}/campaigns", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER')")
    public ResponseEntity<ResponseModel> searchCampaignOfSupplier(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                         @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                         @ModelAttribute SupplierCampaignFilterModel supplierCampaignFilterModel,
                                                         @PathVariable UUID id) {
        ResourceModel<CampaignModel> campaignList = campaignService.searchCampaignsOfSupplier(searchText, paginationRequestModel, supplierCampaignFilterModel, id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(campaignList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
