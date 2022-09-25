package com.tamp_backend.controller;

import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.campaign.*;
import com.tamp_backend.model.campaigncategory.CampaignCategoryModel;
import com.tamp_backend.model.campaigncategory.CampaignCategoryResponseModel;
import com.tamp_backend.model.campaigncategory.CreateCampaignCategoryModel;
import com.tamp_backend.model.campaigncategory.UpdateCampaignCategoryModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.CampaignCategoryService;
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
import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/campaigns")
public class CampaignController {
    private CampaignService campaignService;
    private SystemAdminService systemAdminService;
    private CampaignCategoryService campaignCategoryService;

    public CampaignController(CampaignService campaignService, SystemAdminService systemAdminService, CampaignCategoryService campaignCategoryService) {
        this.campaignService = campaignService;
        this.systemAdminService = systemAdminService;
        this.campaignCategoryService = campaignCategoryService;
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

    /**
     * Update status of campaign
     * @param requestModel
     * @return response model contains updated campaign
     */
    @PatchMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateCampaignStatus(@RequestBody @Valid UpdateStatusCampaignModel requestModel) {
        CampaignModel updatedModel = campaignService.updateCampaignStatus(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Add categories to campaign
     * @param requestModel
     * @param id
     * @return response entity contains model
     */
    @PostMapping(path = "/{id}/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> addCategoryToCampaign(@Valid @RequestBody CreateCampaignCategoryModel requestModel,
                                                               @PathVariable UUID id) {
        List<CampaignCategoryModel> savedModel = campaignCategoryService.addCategoryToCampaign(id, requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("Added successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete category from campaign
     * @param id
     * @param categoryId
     * @return response entity contains model
     */
    @DeleteMapping(path = "/{id}/categories/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteCategoryFromCampaign(
            @PathVariable UUID id, @PathVariable UUID categoryId) {
        CampaignCategoryModel deletedModel = campaignCategoryService.deleteCategoryFromCampaign(id, categoryId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("Deleted successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete category from campaign
     * @param id
     * @param categoryIds
     * @return response entity contains model
     */
    @DeleteMapping(path = "/{id}/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> deleteCategoriesFromCampaign(
            @PathVariable UUID id, @RequestBody List<UUID> categoryIds) {
        List<CampaignCategoryModel> deletedModel = campaignCategoryService.deleteCategoriesFromCampaign(id, categoryIds);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("Deleted successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update category to campaign
     * @param requestModel
     * @param id
     * @return response entity contains model
     */
    @PutMapping(path = "/{id}/categories", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> updateCategoryToCampaign(@Valid @RequestBody UpdateCampaignCategoryModel requestModel,
                                                                  @PathVariable UUID id) {
        CampaignCategoryModel savedModel = campaignCategoryService.updateCategoryToCampaign(id, requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("Updated successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete category from campaign
     * @param id
     * @param categoryId
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}/categories/{categoryId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER','SUPPLIER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> getCategoryByIdOfCampaign(
            @PathVariable UUID id, @PathVariable UUID categoryId) {
        CampaignCategoryResponseModel model = campaignCategoryService.findCategoryByIdOfCampaign(id, categoryId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(model)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
