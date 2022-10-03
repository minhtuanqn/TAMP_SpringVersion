package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.customexception.RangeTimeException;
import com.tamp_backend.entity.*;
;
import com.tamp_backend.metamodel.*;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.campaign.*;
import com.tamp_backend.model.suppliercampaign.SupplierCampaignFilterModel;
import com.tamp_backend.repository.CampaignRepository;
import com.tamp_backend.repository.ProductRepository;
import com.tamp_backend.repository.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CampaignService {
    private CampaignRepository campaignRepository;
    private ModelMapper modelMapper;
    private SupplierRepository supplierRepository;
    private ProductRepository productRepository;

    public CampaignService(CampaignRepository campaignRepository,
                           ModelMapper modelMapper,
                           SupplierRepository supplierRepository,
                           ProductRepository productRepository) {
        this.campaignRepository = campaignRepository;
        this.modelMapper = modelMapper;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
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
     * Specification for joining campaigns of supplier
     * @param supplierId
     * @return specification
     */
    private Specification<CampaignEntity> isJoinedCampaign(UUID supplierId, StatusSearchEnum.SupplierCampaignSearchStatusEnum type) {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<CampaignEntity, CampaignProductEntity> campaignJoins = root.join(CampaignProductEntity_.CAMPAIGN_ID);
            Join<CampaignProductEntity, ProductEntity> productCampaignJoins = root.join(CampaignProductEntity_.PRODUCT_ID);
            Join<ProductEntity, SupplierEntity> supplierProductJoins = root.join(ProductEntity_.SUPPLIER_ID);
            if(type.ordinal() == StatusSearchEnum.SupplierCampaignSearchStatusEnum.JOIN.ordinal()) {
                Predicate belongSupplier = criteriaBuilder.equal(supplierProductJoins.get(ProductEntity_.SUPPLIER_ID), supplierId);
                Predicate joinCondition = criteriaBuilder.equal(productCampaignJoins.get(ProductEntity_.ID), supplierProductJoins.get(ProductEntity_.ID));
                return criteriaBuilder.and(belongSupplier, joinCondition);
            } else if(type.ordinal() == StatusSearchEnum.SupplierCampaignSearchStatusEnum.NOT_JOIN.ordinal()) {
                Predicate belongSupplier = criteriaBuilder.equal(supplierProductJoins.get(ProductEntity_.SUPPLIER_ID), supplierId);
                Predicate joinCondition = criteriaBuilder.notEqual(productCampaignJoins.get(ProductEntity_.ID), supplierProductJoins.get(ProductEntity_.ID));
                return criteriaBuilder.and(belongSupplier, joinCondition);
            } else {
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

    /**
     * Update campaign
     * @param updateCampaignModel
     * @return updated campaign
     */
    public CampaignModel updateCampaign(UpdateCampaignModel updateCampaignModel, String coverPhoto) {
        //Find campaign with id
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(updateCampaignModel.getId());
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("Not found campagin with id"));

        //Check condition for update campaign
        if (campaignEntity.getStatus() != EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal())
            throw new RangeTimeException("Just update campaign when status is active");

        //Check existed campaign with name then update model
        if(campaignRepository.existsByNameAndIdNot(updateCampaignModel.getName(), updateCampaignModel.getId())) {
            throw new DuplicatedEntityException("Duplicate name for campagin");
        }

        // Compare start time and end time
        if(updateCampaignModel.getStartTime().isAfter(updateCampaignModel.getEndTime()))
            throw new RangeTimeException("Check start and end time again");

        //Prepare entity for saving
        if(coverPhoto != null) campaignEntity.setCoverPhoto(coverPhoto);
        campaignEntity.setName(updateCampaignModel.getName());
        campaignEntity.setDescription(updateCampaignModel.getDescription());
        campaignEntity.setStartTime(updateCampaignModel.getStartTime());
        campaignEntity.setEndTime(updateCampaignModel.getEndTime());

        //Save entity to DB
        CampaignEntity savedEntity = campaignRepository.save(campaignEntity);
        return modelMapper.map(savedEntity, CampaignModel.class);
    }

    /**
     * Change stage of campaign
     * @param updateStatusCampaignModel
     * @return updated campaign model
     */
    public CampaignModel updateCampaignStatus(UpdateStatusCampaignModel updateStatusCampaignModel) {
        //Find campaign with id
        Optional<CampaignEntity> optionalCampaignEntity = campaignRepository.findById(updateStatusCampaignModel.getCampaignId());
        CampaignEntity campaignEntity = optionalCampaignEntity.orElseThrow(() -> new NoSuchEntityException("ot found campagin with id"));

        int updatedStatus;
        EntityStatusEnum.CampaignStatusEnum requestStatusEnum = EntityStatusEnum.CampaignStatusEnum
                .values()[updateStatusCampaignModel.getCampaignStatus()];

        switch(requestStatusEnum)
        {
            case DELETED:
                updatedStatus = EntityStatusEnum.CampaignStatusEnum.DELETED.ordinal();
                break;
            case ACTIVE:
                if(campaignEntity.getStatus() != EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal())
                    throw new RangeTimeException("Can not update status with status active");
                updatedStatus = EntityStatusEnum.CampaignStatusEnum.ACTIVE.ordinal();
                break;
            case PRODUCT_PREPARING:
                if (campaignEntity.getStatus() > EntityStatusEnum.CampaignStatusEnum.PRODUCT_PREPARING.ordinal())
                    throw new RangeTimeException("Status is not suitable");
                updatedStatus = requestStatusEnum.ordinal();
                break;
            case READY:
                updatedStatus = requestStatusEnum.ordinal();
                break;
            default:
                throw new NoSuchEntityException("Can not found status of campaign");
        }

        //Save entity to DB
        campaignEntity.setStatus(updatedStatus);
        CampaignEntity savedEntity = campaignRepository.save(campaignEntity);
        return modelMapper.map(savedEntity, CampaignModel.class);
    }

    /**
     * Search campaign of supplier
     * @param searchedValue
     * @param paginationRequestModel
     * @param supplierCampaignFilterModel
     * @param supplierId
     * @return searched campaigns resource
     */
    public ResourceModel<CampaignModel> searchCampaignsOfSupplier(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                                  SupplierCampaignFilterModel supplierCampaignFilterModel, UUID supplierId) {
        PaginationConvertor<CampaignModel, CampaignEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = CampaignEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, CampaignEntity.class);

        //Find all campaigns
        Page<CampaignEntity> campaignEntityPage = campaignRepository.findAll(containsName(searchedValue)
                .and(hasStatus(supplierCampaignFilterModel.getStatusType()))
                .and(containsName(supplierCampaignFilterModel.getName()))
                .and(isSoonerThanStartTime(supplierCampaignFilterModel.isFilterTime(), supplierCampaignFilterModel.getMinTime()))
                .and(isLaterThenEndTime(supplierCampaignFilterModel.isFilterTime(), supplierCampaignFilterModel.getMaxTime()))
                .and(isJoinedCampaign(supplierId,
                        StatusSearchEnum.SupplierCampaignSearchStatusEnum.values()[supplierCampaignFilterModel.getSupplierCampaignStatus()])), pageable);

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
