package com.tamp_backend.controller;

import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.campaign.*;
import com.tamp_backend.model.category.CategoryFilterModel;
import com.tamp_backend.model.category.CategoryModel;
import com.tamp_backend.model.category.UpdateCategoryModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.CampaignService;
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
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/campaigns")
public class CampaignController {
    private CampaignService campaignService;
    private SystemAdminService systemAdminService;

    public CampaignController(CampaignService campaignService, SystemAdminService systemAdminService) {
        this.campaignService = campaignService;
        this.systemAdminService = systemAdminService;
    }

    /**
     * Create new campaign
     * @param requestModel
     * @param coverPhoto
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> createCampaign(@Valid @ModelAttribute CreateCampaignModel requestModel,
                                                        @RequestPart(value = "coverPhoto", required = false) MultipartFile coverPhoto) {
        String username = UserUtils.getCurUsername();
        UUID adminId = systemAdminService.findSystemAdminByUsername(username).getId();
        CampaignModel savedModel = campaignService.createCampaign(requestModel, "", adminId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find campaign by id
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER')")
    public ResponseEntity<ResponseModel> findCampaignById(@PathVariable UUID id) {
        CampaignModel foundModel = campaignService.findCampaignById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(foundModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete campaign
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteCampaign(@PathVariable UUID id) {
        CampaignModel deletedModel = campaignService.deleteCampaign(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search categories by name
     * @param searchText
     * @param paginationRequestModel
     * @param campaignFilterModel
     * @return resource data of category
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'SUPPLIER', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> searchCampaigns(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                          @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                          @ModelAttribute CampaignFilterModel campaignFilterModel) {
        ResourceModel<CampaignModel> campaignList = campaignService.searchCampaigns(searchText, paginationRequestModel, campaignFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(campaignList)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update campaign
     * @param requestModel
     * @param coverPhoto
     * @return response entity contains model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateCampaign(@ModelAttribute @Valid UpdateCampaignModel requestModel,
                                                        @RequestPart(value = "coverPhoto", required = false) MultipartFile coverPhoto) {
        CampaignModel updatedModel = campaignService.updateCampaign(requestModel, "");
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    @PatchMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateCampaignStatus(@RequestBody @Valid UpdateStatusCampaignModel requestModel) {
        CampaignModel updatedModel = campaignService.updateCampaignStatus(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }
}
