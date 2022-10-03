package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AffiliatorTypeEntity;
import com.tamp_backend.entity.PartnerTypeEntity;
import com.tamp_backend.metamodel.AffiliatorTypeEntity_;
import com.tamp_backend.metamodel.PartnerTypeEntity_;
import com.tamp_backend.model.affiliatortype.AffiliatorTypeFilterModel;
import com.tamp_backend.model.affiliatortype.AffiliatorTypeModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.affiliatortype.CreateAffiliatorTypeModel;
import com.tamp_backend.model.affiliatortype.UpdateAffiliatorTypeModel;
import com.tamp_backend.model.partnertype.PartnerTypeModel;
import com.tamp_backend.repository.AffiliatorTypeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class AffiliatorTypeService {
    private AffiliatorTypeRepository affiliatorTypeRepository;
    private ModelMapper modelMapper;

    public AffiliatorTypeService(AffiliatorTypeRepository affiliatorTypeRepository,
                                 ModelMapper modelMapper) {
        this.affiliatorTypeRepository = affiliatorTypeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return specification
     */

    private Specification<AffiliatorTypeEntity> containsTypeName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(AffiliatorTypeEntity_.TYPE_NAME), pattern);
        });
    }

    /**
     * Specification for hasStatus
     * @param searchStatus
     * @return specification
     */
    private Specification<AffiliatorTypeEntity> hasStatus(int searchStatus) {
        return ((root, query, criteriaBuilder) -> {
            if(searchStatus < StatusSearchEnum.AffiliatorTypeStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(AffiliatorTypeEntity_.STATUS), searchStatus);
            } else {
                return criteriaBuilder.lessThan(root.get(AffiliatorTypeEntity_.STATUS),
                        StatusSearchEnum.AffiliatorTypeStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * search affiliatorType like name
     * @param searchedValue
     * @param paginationRequestModel
     * @param affiliatorTypeFilterModel
     * @return resource of data
     */
    public ResourceModel<AffiliatorTypeModel> searchAffiliatorTypes(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                                    AffiliatorTypeFilterModel affiliatorTypeFilterModel) {
        PaginationConvertor<AffiliatorTypeModel, AffiliatorTypeEntity> paginationConvertor = new PaginationConvertor<>();
        String defaultSortBy = AffiliatorTypeEntity_.TYPE_NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, AffiliatorTypeEntity.class);

        //Find all affiliator types
        Page<AffiliatorTypeEntity> affiliatorTypeEntityPage = affiliatorTypeRepository
                .findAll(containsTypeName(searchedValue)
                        .and(containsTypeName(affiliatorTypeFilterModel.getTypeName()))
                        .and(hasStatus(affiliatorTypeFilterModel.getStatusType())), pageable);

        //Convert list of affiliator types entity to list of affiliator types model
        List<AffiliatorTypeModel> affiliatorTypeModels = new ArrayList<>();
        for (AffiliatorTypeEntity entity : affiliatorTypeEntityPage) {
            affiliatorTypeModels.add(modelMapper.map(entity, AffiliatorTypeModel.class));
        }

        //Prepare resource for return
        ResourceModel<AffiliatorTypeModel> resource = new ResourceModel<>();
        resource.setData(affiliatorTypeModels);
        resource.setSearchText(searchedValue);
        resource.setSortBy(defaultSortBy);
        resource.setSortType(paginationRequestModel.getSortType());
        paginationConvertor.buildPagination(paginationRequestModel, affiliatorTypeEntityPage, resource);
        return resource;
    }

    /**
     * create a affiliator type
     *
     * @param createAffiliatorTypeModel
     * @return created model
     */
    public AffiliatorTypeModel createAffiliatorType(CreateAffiliatorTypeModel createAffiliatorTypeModel) {
        // Check exist affiliator type
        if (affiliatorTypeRepository.existsByTypeName(createAffiliatorTypeModel.getTypeName())) {
            throw new DuplicatedEntityException("Duplicate name for affiliator type");
        }

        //Prepare entity
        AffiliatorTypeEntity entity = modelMapper.map(createAffiliatorTypeModel, AffiliatorTypeEntity.class);
        entity.setStatus(EntityStatusEnum.AffiliatorTypeStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        AffiliatorTypeEntity savedEntity = affiliatorTypeRepository.save(entity);
        return modelMapper.map(savedEntity, AffiliatorTypeModel.class);
    }

    /**
     * Find a affiliator type by id
     *
     * @param id
     * @return found model
     */

    public AffiliatorTypeModel findAffiliatorTypeById(UUID id) {
        //Find affiliator type by id
        Optional<AffiliatorTypeEntity> searchedAffiliatorTypeOptional = affiliatorTypeRepository.findById(id);
        AffiliatorTypeEntity affiliatorTypeEntity = searchedAffiliatorTypeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found affiliator type with id"));
        return modelMapper.map(affiliatorTypeEntity, AffiliatorTypeModel.class);
    }

    /**
     * Update affiliator type
     * @param updateAffiliatorTypeModel
     * @return updated affiliator type
     */

    public AffiliatorTypeModel updateAffType(UpdateAffiliatorTypeModel updateAffiliatorTypeModel){
        // Find affiliator type with id
        Optional<AffiliatorTypeEntity> searchedAffiliatorTypeOptional = affiliatorTypeRepository
                .findById(updateAffiliatorTypeModel.getId());
        AffiliatorTypeEntity searchAffiliatorTypeEntity = searchedAffiliatorTypeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found affiliator type with id"));

        //Check existed affiliator type with name then update model
        if(affiliatorTypeRepository.existsByTypeNameAndIdNot(updateAffiliatorTypeModel.getTypeName(),
                updateAffiliatorTypeModel.getId())){
            throw new DuplicatedEntityException("Duplicate name for affiliator type");
        }

        //Save entity to DB
        AffiliatorTypeEntity savedEntity = affiliatorTypeRepository
                .save(modelMapper.map(updateAffiliatorTypeModel, AffiliatorTypeEntity.class));
        return modelMapper.map(savedEntity, AffiliatorTypeModel.class);
    }

    /**
     * delete a affiliator type
     * @param id
     * @return delete model
     */

    public AffiliatorTypeModel deleteAffiliatorType(UUID id){
        // Find affiliator type with id
        Optional<AffiliatorTypeEntity> deletedAffiliatorTypeOptional = affiliatorTypeRepository.findById(id);
        AffiliatorTypeEntity deletedAffiliatorTypeEntity = deletedAffiliatorTypeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found affiliator type with id"));

        //Set status for entity
        deletedAffiliatorTypeEntity.setStatus(EntityStatusEnum.AffiliatorTypeStatusEnum.DISABLE.ordinal());

        //Update status of affiliator type
        AffiliatorTypeEntity responseEntity = affiliatorTypeRepository.save(deletedAffiliatorTypeEntity);
        return modelMapper.map(responseEntity, AffiliatorTypeModel.class);
    }

    /**
     * Delete affiliator types by ids
     * @param ids
     * @return deleted affiliator type models
     */
    public List<AffiliatorTypeModel> deleteAffiliatortypes(List<UUID> ids) {
        if(ids == null) throw new NoSuchEntityException("Not found any affiliator type");
        List<AffiliatorTypeModel> deletedModels = new ArrayList<>();
        for (UUID id : ids) {
            AffiliatorTypeModel affiliatorTypeModel = deleteAffiliatorType(id);
            deletedModels.add(affiliatorTypeModel);
        }
        return deletedModels;
    }

}
