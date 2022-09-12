package com.tamp_backend.service;


import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.PartnerTypeEntity;
import com.tamp_backend.metamodel.PartnerTypeEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.PartnerTypeModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.repository.PartnerTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PartnerTypeService {
    private PartnerTypeRepository partnerTypeRepository;

    public PartnerTypeService(PartnerTypeRepository partnerTypeRepository) {
        this.partnerTypeRepository = partnerTypeRepository;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return specification
     */

    private Specification<PartnerTypeEntity> containsTypeName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(PartnerTypeEntity_.TYPE_NAME), pattern);
        });
    }

    /**
     * search partnerType like name
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<PartnerTypeModel> searchPartnerTypes(String searchedValue, PaginationRequestModel paginationRequestModel) {
        PaginationConvertor<PartnerTypeModel, PartnerTypeEntity> paginationConvertor = new PaginationConvertor<>();
        String defaultSortBy = PartnerTypeEntity_.TYPE_NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, PartnerTypeEntity.class);

        //Find all partner types
        Page<PartnerTypeEntity> partnerTypeEntityPage = partnerTypeRepository.findAll(containsTypeName(searchedValue), pageable);

        //Convert list of partner types entity to list of partner types model
        List<PartnerTypeModel> partnerTypeModels = new ArrayList<>();
        for (PartnerTypeEntity entity : partnerTypeEntityPage) {
            partnerTypeModels.add(new PartnerTypeModel(entity));
        }

        //Prepare resource for return
        ResourceModel<PartnerTypeModel> resource = new ResourceModel<>();
        resource.setData(partnerTypeModels);
        paginationConvertor.buildPagination(paginationRequestModel, partnerTypeEntityPage, resource);
        return resource;

    }

    /**
     * create a partner type
     *
     * @param model
     * @return created model
     */
    public PartnerTypeModel createPartnerType(PartnerTypeModel model) {
        // Check exist partner type
        if (partnerTypeRepository.existsPartnerTypeEntitiesByTypeName(model.getTypeName())) {
            throw new DuplicatedEntityException("This partner type already exist");
        }

        //Set id for model is null
        if (model.getId() != null) {
            model.setId(UUID.randomUUID());
        }

        //Prepare entity
        PartnerTypeEntity entity = new PartnerTypeEntity(model);
        entity.setStatus(EntityStatusEnum.PartnerTypeStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        PartnerTypeEntity savedEntity = partnerTypeRepository.save(entity);
        return model;

    }

    /**
     * Find a partner type by id
     * @param id
     * @return found model
     */

    public PartnerTypeModel findPartnerTypeById(UUID id){
        //Find partner type by id
        Optional<PartnerTypeEntity> searchedPartnerTypeOptional = partnerTypeRepository.findById(id);
        PartnerTypeEntity partnerTypeEntity = searchedPartnerTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found partner type"));
        return new PartnerTypeModel(partnerTypeEntity);
    }

    /**
     * Update partner type
     * @param id
     * @param partnerTypeModel
     * @return updated partner type
     */

    public PartnerTypeModel updatePartnerType(UUID id, PartnerTypeModel partnerTypeModel){
        // Find partner type with id
        Optional<PartnerTypeEntity> searchedPartnerTypeOptional = partnerTypeRepository.findById(id);
        PartnerTypeEntity searchPartnerTypeEntity = searchedPartnerTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found partner type"));

        //Check existed partner type with name then update model
        if(partnerTypeRepository.existsPartnerTypeEntitiesByTypeNameAndIdNot(partnerTypeModel.getTypeName(), id)){
            throw new DuplicatedEntityException("This partner type existed");
        }

        //Prepare entity for saving to DB
        partnerTypeModel.setId(id);

        //Save entity to DB
        PartnerTypeEntity saveEntity = partnerTypeRepository.save(new PartnerTypeEntity(partnerTypeModel));
        return new PartnerTypeModel(saveEntity);
    }

    /**
     * delete a partner type
     * @param id
     * @return delete model
     */

    public PartnerTypeModel deletePartnerType(UUID id){
        // Find partner type with id
        Optional<PartnerTypeEntity> deletedPartnerTypeOptional = partnerTypeRepository.findById(id);
        PartnerTypeEntity deletedPartnerTypeEntity = deletedPartnerTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found partner type"));

        //Set status for entity
        deletedPartnerTypeEntity.setStatus(EntityStatusEnum.PartnerTypeStatusEnum.DISABLE.ordinal());

        //Update status of partner type
        PartnerTypeEntity responseEntity = partnerTypeRepository.save(deletedPartnerTypeEntity);
        return new PartnerTypeModel(responseEntity);
    }


}
