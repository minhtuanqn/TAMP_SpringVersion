package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.AffiliatorTypeEntity;
import com.tamp_backend.metamodel.AffiliatorTypeEntity_;
import com.tamp_backend.model.affiliatortype.AffiliatorTypeModel;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.repository.AffiliatorTypeRepository;
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

    public AffiliatorTypeService(AffiliatorTypeRepository affiliatorTypeRepository) {
        this.affiliatorTypeRepository = affiliatorTypeRepository;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return specification
     */

    private Specification<AffiliatorTypeEntity> containsTypeName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(AffiliatorTypeEntity_.TYPE_NAME), pattern);
        });
    }

    /**
     * search affiliatorType like name
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<AffiliatorTypeModel> searchAffiliatorTypes(String searchedValue, PaginationRequestModel paginationRequestModel) {
        PaginationConvertor<AffiliatorTypeModel, AffiliatorTypeEntity> paginationConvertor = new PaginationConvertor<>();
        String defaultSortBy = AffiliatorTypeEntity_.TYPE_NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, AffiliatorTypeEntity.class);

        //Find all affiliator types
        Page<AffiliatorTypeEntity> affiliatorTypeEntityPage = affiliatorTypeRepository.findAll(containsTypeName(searchedValue), pageable);

        //Convert list of affiliator types entity to list of affiliator types model
        List<AffiliatorTypeModel> affiliatorTypeModels = new ArrayList<>();
        for (AffiliatorTypeEntity entity : affiliatorTypeEntityPage) {
            affiliatorTypeModels.add(new AffiliatorTypeModel(entity));
        }

        //Prepare resource for return
        ResourceModel<AffiliatorTypeModel> resource = new ResourceModel<>();
        resource.setData(affiliatorTypeModels);
        paginationConvertor.buildPagination(paginationRequestModel, affiliatorTypeEntityPage, resource);
        return resource;
    }

    /**
     * create a affiliator type
     *
     * @param model
     * @return created model
     */
    public AffiliatorTypeModel createAffiliatorType(AffiliatorTypeModel model) {
        // Check exist affiliator type
        if (affiliatorTypeRepository.existsAffiliatorTypeEntitiesByTypeName(model.getTypeName())) {
            throw new DuplicatedEntityException("This affiliator type already exist");
        }

        //Set id for model is null
        if (model.getId() != null) {
            model.setId(UUID.randomUUID());
        }

        //Prepare entity
        AffiliatorTypeEntity entity = new AffiliatorTypeEntity(model);
        entity.setStatus(EntityStatusEnum.AffiliatorTypeStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        AffiliatorTypeEntity savedEntity = affiliatorTypeRepository.save(entity);
        return model;
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
        AffiliatorTypeEntity affiliatorTypeEntity = searchedAffiliatorTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found affiliator type"));
        return new AffiliatorTypeModel(affiliatorTypeEntity);
    }

    /**
     * Update affiliator type
     * @param id
     * @param affiliatorTypeModel
     * @return updated affiliator type
     */

    public AffiliatorTypeModel updateAffType(UUID id, AffiliatorTypeModel affiliatorTypeModel){
        // Find affiliator type with id
        Optional<AffiliatorTypeEntity> searchedAffiliatorTypeOptional = affiliatorTypeRepository.findById(id);
        AffiliatorTypeEntity searchAffiliatorTypeEntity = searchedAffiliatorTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found affiliator type"));

        //Check existed affiliator type with name then update model
        if(affiliatorTypeRepository.existsAffiliatorTypeEntitiesByTypeNameAndIdIsNot(affiliatorTypeModel.getTypeName(), id)){
            throw new DuplicatedEntityException("This affiliator type existed");
        }

        //Prepare entity for saving to DB
        affiliatorTypeModel.setId(id);

        //Save entity to DB
        AffiliatorTypeEntity saveEntity = affiliatorTypeRepository.save(new AffiliatorTypeEntity(affiliatorTypeModel));
        return new AffiliatorTypeModel(saveEntity);
    }

    /**
     * delete a affiliator type
     * @param id
     * @return delete model
     */

    public AffiliatorTypeModel deleteAffiliatorType(UUID id){
        // Find affiliator type with id
        Optional<AffiliatorTypeEntity> deletedAffiliatorTypeOptional = affiliatorTypeRepository.findById(id);
        AffiliatorTypeEntity deletedAffiliatorTypeEntity = deletedAffiliatorTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found affiliator type"));

        //Set status for entity
        deletedAffiliatorTypeEntity.setStatus(EntityStatusEnum.AffiliatorTypeStatusEnum.DISABLE.ordinal());

        //Update status of affiliator type
        AffiliatorTypeEntity responseEntity = affiliatorTypeRepository.save(deletedAffiliatorTypeEntity);
        return new AffiliatorTypeModel(responseEntity);
    }

}
