package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.customexception.RangeTimeException;
import com.tamp_backend.entity.CampaignEntity;;
import com.tamp_backend.metamodel.CampaignEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.campaign.CampaignFilterModel;
import com.tamp_backend.model.campaign.CampaignModel;
import com.tamp_backend.model.campaign.CreateCampaignModel;
import com.tamp_backend.repository.CampaignRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CampaignService {
    private CampaignRepository campaignRepository;

    private ModelMapper modelMapper;

    public CampaignService(CampaignRepository campaignRepository, ModelMapper modelMapper) {
        this.campaignRepository = campaignRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new campaign
     * @param createCampaignModel
     * @param coverPhotoUrl
     * @param adminId
     * @return created campaign model
     */
    public CampaignModel createCampaign(CreateCampaignModel createCampaignModel, String coverPhotoUrl, UUID adminId) {
        // Check exist campaign name in DB
        if (campaignRepository.existsByName(createCampaignModel.getName()))
            throw new DuplicatedEntityException("Duplicated name of campaign");

        // Compare start time and end time
        if(createCampaignModel.getStartTime().isAfter(createCampaignModel.getEndTime()))
            throw new RangeTimeException("Check start and end time again");

        //Save new information of campaign
        CampaignEntity createdCampaign = modelMapper.map(createCampaignModel, CampaignEntity.class);
        createdCampaign.setCoverPhoto(coverPhotoUrl);
        createdCampaign.setStatus(EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal());
        createdCampaign.setCreateBy(adminId);

        //Save entity to DB
        CampaignEntity savedEntity = campaignRepository.save(createdCampaign);
        CampaignModel responseCampaignModel = modelMapper.map(savedEntity, CampaignModel.class);
        return responseCampaignModel;
    }

    /**
     * Find a campaign by id
     * @param id
     * @return found model
     */
    public CampaignModel findCampaignById(UUID id) {
        //Find campaign with id
        Optional<CampaignEntity> searchedCampaignOptional = campaignRepository.findById(id);
        CampaignEntity campaignEntity = searchedCampaignOptional.orElseThrow(() -> new NoSuchEntityException("Not found campaign"));
        return modelMapper.map(campaignEntity, CampaignModel.class);
    }

    /**
     * Delete a campaign
     * @param id
     * @return delete model
     */
    public CampaignModel deleteCampaign(UUID id) {
        //Find campaign with id
        Optional<CampaignEntity> deletedCampaignOptional = campaignRepository.findById(id);
        CampaignEntity deletedCampaignEntity = deletedCampaignOptional.orElseThrow(() -> new NoSuchEntityException("Not found campaign with id"));

        //Set status for entity
        deletedCampaignEntity.setStatus(EntityStatusEnum.CampaignStatusEnum.DELETED.ordinal());

        //Update status of campaign
        CampaignEntity responseEntity = campaignRepository.save(deletedCampaignEntity);
        return modelMapper.map(responseEntity, CampaignModel.class);
    }

    /**
     * Specification for search name
     * @param searchedValue
     * @return specification
     */
    private Specification<CampaignEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(CampaignEntity_.NAME), pattern);
        });
    }

    /**
     * Specification for hasStatus
     * @param searchStatus
     * @return specification
     */
    private Specification<CampaignEntity> hasStatus(int searchStatus) {
        return ((root, query, criteriaBuilder) -> {
            if(searchStatus < StatusSearchEnum.CampaignStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(CampaignEntity_.STATUS), searchStatus);
            } else{
                return criteriaBuilder.lessThan(root.get(CampaignEntity_.STATUS), StatusSearchEnum.CampaignStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * Specification for sooner than start time
     * @param startTime
     * @return specification
     */
    private Specification<CampaignEntity> isSoonerThanStartTime(boolean isTimeFilter, LocalDateTime startTime) {
        return ((root, query, criteriaBuilder) -> {
            if(isTimeFilter) {
                return criteriaBuilder.lessThanOrEqualTo(root.get(CampaignEntity_.START_TIME), startTime);
            } else{
                return criteriaBuilder.lessThan(root.get(CampaignEntity_.STATUS), StatusSearchEnum.CampaignStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * Specification for later then end time
     * @param endTime
     * @return specification
     */
    private Specification<CampaignEntity> isLaterThenEndTime(boolean isTimeFilter, LocalDateTime endTime) {
        return ((root, query, criteriaBuilder) -> {
            if(isTimeFilter) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(CampaignEntity_.END_TIME), endTime);
            } else{
                return criteriaBuilder.lessThan(root.get(CampaignEntity_.STATUS), StatusSearchEnum.CampaignStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * search campaign with condition
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<CampaignModel> searchCampaigns(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                         CampaignFilterModel campaignFilterModel) {
        PaginationConvertor<CampaignModel, CampaignEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = CampaignEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, CampaignEntity.class);

        //Find all campaigns
        Page<CampaignEntity> campaignEntityPage = campaignRepository.findAll(containsName(searchedValue)
                .and(hasStatus(campaignFilterModel.getStatusType()))
                .and(containsName(campaignFilterModel.getName()))
                .and(isSoonerThanStartTime(campaignFilterModel.isFilterTime(), campaignFilterModel.getMinTime()))
                .and(isLaterThenEndTime(campaignFilterModel.isFilterTime(), campaignFilterModel.getMaxTime())), pageable);

        //Convert list of campaigns entity to list of campaigns model
        List<CampaignModel> campaignModels = new ArrayList<>();
        for (CampaignEntity entity : campaignEntityPage) {
            campaignModels.add(modelMapper.map(entity, CampaignModel.class));
        }

        //Prepare resource for return
        ResourceModel<CampaignModel> resource = new ResourceModel<>();
        resource.setData(campaignModels);
        resource.setSearchText(searchedValue);
        resource.setSortBy(defaultSortBy);
        resource.setSortType(paginationRequestModel.getSortType());
        paginationConvertor.buildPagination(paginationRequestModel, campaignEntityPage, resource);
        return resource;
    }

}
