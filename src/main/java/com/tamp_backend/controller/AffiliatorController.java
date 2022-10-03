package com.tamp_backend.controller;

import com.tamp_backend.constant.UserEnum;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.ResponseModel;
import com.tamp_backend.model.affiliator.AffiliatorFilterModel;
import com.tamp_backend.model.affiliator.AffiliatorModel;
import com.tamp_backend.model.affiliator.CreateAffiliatorModel;
import com.tamp_backend.model.affiliator.UpdateAffiliatorModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.AffiliatorService;
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
@RequestMapping(path = "/affiliators")
public class AffiliatorController {
    private AffiliatorService affiliatorService;
    private SystemAdminService systemAdminService;
    private PartnerService partnerService;


    public AffiliatorController(AffiliatorService affiliatorService, SystemAdminService systemAdminService, PartnerService partnerService) {
        this.affiliatorService = affiliatorService;
        this.systemAdminService = systemAdminService;
        this.partnerService = partnerService;
    }

    /**
     * Create new affiliator
     *
     * @param requestModel
     * @param avatar
     * @return response model contains created affiliator model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER')")
    public ResponseEntity<ResponseModel> createAffiliator(@Valid @ModelAttribute CreateAffiliatorModel requestModel, @RequestPart MultipartFile avatar) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = null;
        if (curRole.equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        } else if (curRole.equals(UserEnum.RoleEnum.PARTNER.toString())) {
            curUserId = partnerService.findPartnerByUsername(username).getId();
        } else {
            curUserId = affiliatorService.findAffiliatorByUsername(username).getId();
        }
        AffiliatorModel savedAffiliatorModel = affiliatorService.createAffiliator(requestModel, "", Enum.valueOf(UserEnum.RoleEnum.class, curRole), curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(savedAffiliatorModel).message("Created successfully");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find affiliator by id
     *
     * @param id
     * @return response model contains affiliator model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> findAffiliatorById(@PathVariable UUID id) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = null;
        if (curRole.equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        } else if (curRole.equals(UserEnum.RoleEnum.PARTNER.toString())) {
            curUserId = partnerService.findPartnerByUsername(username).getId();
        } else {
            curUserId = affiliatorService.findAffiliatorByUsername(username).getId();
        }
        AffiliatorModel affiliatorModel = affiliatorService.findAffiliatorById(id, Enum.valueOf(UserEnum.RoleEnum.class, curRole), curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(affiliatorModel).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete a affiliator model
     *
     * @param id
     * @return response model contains deleted affiliator model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER')")
    public ResponseEntity<ResponseModel> deleteAffiliator(@PathVariable UUID id) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = null;
        if (curRole.equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        } else {
            curUserId = partnerService.findPartnerByUsername(username).getId();
        }
        AffiliatorModel deletedAffiliatorModel = affiliatorService.deleteAffiliator(id, Enum.valueOf(UserEnum.RoleEnum.class, curRole), curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(deletedAffiliatorModel).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete affiliators by list of id
     *
     * @param ids
     * @return response model contains deleted affiliators
     */
    @DeleteMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER')")
    public ResponseEntity<ResponseModel> deleteAffiliators(@RequestBody List<UUID> ids) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = null;
        if (curRole.equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        } else {
            curUserId = partnerService.findPartnerByUsername(username).getId();
        }
        List<AffiliatorModel> affiliatorModels = affiliatorService.deleteAffiliatorsByIds(ids, Enum.valueOf(UserEnum.RoleEnum.class, curRole), curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(affiliatorModels).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search affiliators by name
     *
     * @param searchText
     * @param paginationRequestModel
     * @return resource data of affiliator
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<ResponseModel> searchAffiliators(@RequestParam(value = "searchText", defaultValue = "") String searchText, @RequestPagingParam PaginationRequestModel paginationRequestModel, @ModelAttribute AffiliatorFilterModel affiliatorFilterModel) {
        ResourceModel<AffiliatorModel> affiliatorList = affiliatorService.searchAffiliators(searchText, paginationRequestModel, affiliatorFilterModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value()).data(affiliatorList).message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Update affiliator
     *
     * @param requestModel
     * @param avatar
     * @return updated affiliator model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN', 'PARTNER', 'AFFILIATOR')")
    public ResponseEntity<ResponseModel> updateAffiliator(@Valid @ModelAttribute UpdateAffiliatorModel requestModel, @RequestPart MultipartFile avatar) {
        String username = UserUtils.getCurUsername();
        String curRole = UserUtils.getCurRole();
        UUID curUserId = null;
        if (curRole.equals(UserEnum.RoleEnum.SYSTEM_ADMIN.toString())) {
            curUserId = systemAdminService.findSystemAdminByUsername(username).getId();
        } else if (curRole.equals(UserEnum.RoleEnum.PARTNER.toString())) {
            curUserId = partnerService.findPartnerByUsername(username).getId();
        } else {
            curUserId = affiliatorService.findAffiliatorByUsername(username).getId();
        }
        AffiliatorModel updatedModel = affiliatorService.updateAffiliator(requestModel, null, Enum.valueOf(UserEnum.RoleEnum.class, curRole), curUserId);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

}
