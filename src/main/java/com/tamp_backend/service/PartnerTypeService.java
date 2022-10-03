package com.tamp_backend.service;


import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.constant.StatusSearchEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.PartnerTypeEntity;
import com.tamp_backend.metamodel.PartnerTypeEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.partnertype.CreatePartnerTypeModel;
import com.tamp_backend.model.partnertype.PartnerTypeFilterModel;
import com.tamp_backend.model.partnertype.PartnerTypeModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.partnertype.UpdatePartnerTypeModel;
import com.tamp_backend.repository.PartnerTypeRepository;
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
public class PartnerTypeService {
    private PartnerTypeRepository partnerTypeRepository;
    private ModelMapper modelMapper;

    public PartnerTypeService(PartnerTypeRepository partnerTypeRepository,
                              ModelMapper modelMapper) {
        this.partnerTypeRepository = partnerTypeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Specification for search name
     *
     * @param searchedValue
     * @return specification
     */
    private Specification<PartnerTypeEntity> containsTypeName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchedValue != null ? "%" + searchedValue + "%" : "%" + "%";
            return criteriaBuilder.like(root.get(PartnerTypeEntity_.TYPE_NAME), pattern);
        });
    }

    /**
     * Specification for hasStatus
     * @param searchStatus
     * @return specification
     */
    private Specification<PartnerTypeEntity> hasStatus(int searchStatus) {
        return ((root, query, criteriaBuilder) -> {
            if(searchStatus < StatusSearchEnum.PartnerTypeStatusSearchEnum.ALL.ordinal()) {
                return criteriaBuilder.equal(root.get(PartnerTypeEntity_.STATUS), searchStatus);
            } else {
                return criteriaBuilder.lessThan(root.get(PartnerTypeEntity_.STATUS),
                        StatusSearchEnum.PartnerTypeStatusSearchEnum.ALL.ordinal());
            }
        });
    }

    /**
     * search partnerType like name
     *
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<PartnerTypeModel> searchPartnerTypes(String searchedValue, PaginationRequestModel paginationRequestModel,
                                                              PartnerTypeFilterModel partnerTypeFilterModel) {
        PaginationConvertor<PartnerTypeModel, PartnerTypeEntity> paginationConvertor = new PaginationConvertor<>();
        String defaultSortBy = PartnerTypeEntity_.TYPE_NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, PartnerTypeEntity.class);

        //Find all partner types
        Page<PartnerTypeEntity> partnerTypeEntityPage = partnerTypeRepository
                .findAll(containsTypeName(searchedValue)
                        .and(containsTypeName(partnerTypeFilterModel.getTypeName()))
                        .and(hasStatus(partnerTypeFilterModel.getStatusType())), pageable);

        //Convert list of partner types entity to list of partner types model
        List<PartnerTypeModel> partnerTypeModels = new ArrayList<>();
        for (PartnerTypeEntity entity : partnerTypeEntityPage) {
            partnerTypeModels.add(modelMapper.map(entity, PartnerTypeModel.class));
        }

        //Prepare resource for return
        ResourceModel<PartnerTypeModel> resource = new ResourceModel<>();
        resource.setData(partnerTypeModels);
        resource.setSearchText(searchedValue);
        resource.setSortBy(defaultSortBy);
        resource.setSortType(paginationRequestModel.getSortType());
        paginationConvertor.buildPagination(paginationRequestModel, partnerTypeEntityPage, resource);
        return resource;

    }

    /**
     * create a partner type
     *
     * @param createPartnerTypeModel
     * @return created model
     */
    public PartnerTypeModel createPartnerType(CreatePartnerTypeModel createPartnerTypeModel) {
        // Check exist partner type
        if (partnerTypeRepository.existsPartnerTypeEntitiesByTypeName(createPartnerTypeModel.getTypeName())) {
            throw new DuplicatedEntityException("Duplicated name of partner type");
        }

        //Prepare entity
        PartnerTypeEntity entity = modelMapper.map(createPartnerTypeModel, PartnerTypeEntity.class);
        entity.setStatus(EntityStatusEnum.PartnerTypeStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        PartnerTypeEntity savedEntity = partnerTypeRepository.save(entity);
        return modelMapper.map(savedEntity, PartnerTypeModel.class);

    }

    /**
     * Find a partner type by id
     * @param id
     * @return found model
     */
    public PartnerTypeModel findPartnerTypeById(UUID id){
        //Find partner type by id
        Optional<PartnerTypeEntity> searchedPartnerTypeOptional = partnerTypeRepository.findById(id);
        PartnerTypeEntity partnerTypeEntity = searchedPartnerTypeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found partnerType with id"));
        return modelMapper.map(partnerTypeEntity, PartnerTypeModel.class);
    }

    /**
     * Update partner type
     * @param updatePartnerTypeModel
     * @return updated partner type
     */
    public PartnerTypeModel updatePartnerType(UpdatePartnerTypeModel updatePartnerTypeModel){
        // Find partner type with id
        Optional<PartnerTypeEntity> searchedPartnerTypeOptional = partnerTypeRepository.findById(updatePartnerTypeModel.getId());
        PartnerTypeEntity searchPartnerTypeEntity = searchedPartnerTypeOptional.orElseThrow(() -> new NoSuchEntityException("Not found partnerType with id"));

        //Check existed partner type with name then update model
        if(partnerTypeRepository.existsPartnerTypeEntitiesByTypeNameAndIdNot(updatePartnerTypeModel.getTypeName(), updatePartnerTypeModel.getId())){
            throw new DuplicatedEntityException("Duplicate name for partner type");
        }

        //Save entity to DB
        PartnerTypeEntity saveEntity = partnerTypeRepository.save(modelMapper.map(updatePartnerTypeModel, PartnerTypeEntity.class));
        return modelMapper.map(saveEntity, PartnerTypeModel.class);
    }

    /**
     * delete a partner type
     * @param id
     * @return delete model
     */
    public PartnerTypeModel deletePartnerType(UUID id){
        // Find partner type with id
        Optional<PartnerTypeEntity> deletedPartnerTypeOptional = partnerTypeRepository.findById(id);
        PartnerTypeEntity deletedPartnerTypeEntity = deletedPartnerTypeOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found partnerType with id"));

        //Set status for entity
        deletedPartnerTypeEntity.setStatus(EntityStatusEnum.PartnerTypeStatusEnum.DISABLE.ordinal());

        //Update status of partner type
        PartnerTypeEntity responseEntity = partnerTypeRepository.save(deletedPartnerTypeEntity);
        return modelMapper.map(responseEntity, PartnerTypeModel.class);
    }

    /**
     * Delete partnertype by ids
     * @param ids
     * @return deleted partnertype models
     */
    public List<PartnerTypeModel> deleteParnertypes(List<UUID> ids) {
        if(ids == null) throw new NoSuchEntityException("Not found any partner type");
        List<PartnerTypeModel> deletedModels = new ArrayList<>();
        for (UUID id : ids) {
            PartnerTypeModel partnerTypeModel = deletePartnerType(id);
            deletedModels.add(partnerTypeModel);
        }
        return deletedModels;
    }

}
