package com.tamp_backend.service;

import com.tamp_backend.constant.EntityStatusEnum;
import com.tamp_backend.convertor.PaginationConvertor;
import com.tamp_backend.customexception.DuplicatedEntityException;
import com.tamp_backend.customexception.NoSuchEntityException;
import com.tamp_backend.entity.StationEntity;
import com.tamp_backend.metamodel.StationEntity_;
import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.StationModel;
import com.tamp_backend.repository.StationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StationService {
    private StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    /**
     * create a station
     * @param model
     * @return created model
     */
    public StationModel createStation(StationModel model) {
        //Prepare entity
        StationEntity entity = new StationEntity(model);
        entity.setStatus(EntityStatusEnum.StationStatusEnum.ACTIVE.ordinal());

        //Save entity to DB
        StationEntity savedEntity = stationRepository.save(entity);
        model = new StationModel(savedEntity);
        return model;
    }

    /**
     * delete a station
     * @param id
     * @return delete model
     */
    public StationModel deleteStation(UUID id) {
        //Find station with id
        Optional<StationEntity> deletedStationOptional = stationRepository.findById(id);
        StationEntity deletedStationEntity = deletedStationOptional.orElseThrow(() -> new NoSuchEntityException("Not found station"));

        //Set status for entity
        deletedStationEntity.setStatus(EntityStatusEnum.StationStatusEnum.DISABLE.ordinal());

        //Update status of station
        StationEntity responseEntity = stationRepository.save(deletedStationEntity);
        return new StationModel(responseEntity);
    }

    /**
     * Find a station by id
     * @param id
     * @return found model
     */
    public StationModel findStationById(UUID id) {
        //Find station with id
        Optional<StationEntity> searchedStationOptional = stationRepository.findById(id);
        StationEntity stationEntity = searchedStationOptional.orElseThrow(() -> new NoSuchEntityException("Not found station"));
        return new StationModel(stationEntity);
    }

    /**
     * Update station
     * @param id
     * @param stationModel
     * @return updated station
     */
    public StationModel updateStation(UUID id, StationModel stationModel) {
        //Find station with id
        Optional<StationEntity> searchedStationOptional = stationRepository.findById(id);
        StationEntity searchedStationEntity = searchedStationOptional.orElseThrow(() -> new NoSuchEntityException("Not found station"));

        //Prepare entity for saving to DB
        stationModel.setId(id);

        //Save entity to DB
        StationEntity savedEntity = stationRepository.save(new StationEntity(stationModel));
        return new StationModel(savedEntity);
    }

    /**
     * Specification for search name
     * @param searchedValue
     * @return specification
     */
    private Specification<StationEntity> containsName(String searchedValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = "%" + searchedValue + "%";
            return criteriaBuilder.like(root.get(StationEntity_.NAME), pattern);
        });
    }

    /**
     * search station like name
     * @param searchedValue
     * @param paginationRequestModel
     * @return resource of data
     */
    public ResourceModel<StationModel> searchStations(String searchedValue, PaginationRequestModel paginationRequestModel) {
        PaginationConvertor<StationModel, StationEntity> paginationConvertor = new PaginationConvertor<>();

        String defaultSortBy = StationEntity_.NAME;
        Pageable pageable = paginationConvertor.convertToPageable(paginationRequestModel, defaultSortBy, StationEntity.class);

        //Find all stations
        Page<StationEntity> stationEntityPage = stationRepository.findAll(containsName(searchedValue), pageable);

        //Convert list of station entity to list of stations model
        List<StationModel> stationModels = new ArrayList<>();
        for (StationEntity entity : stationEntityPage) {
            stationModels.add(new StationModel(entity));
        }

        //Prepare resource for return
        ResourceModel<StationModel> resource = new ResourceModel<>();
        resource.setData(stationModels);
        paginationConvertor.buildPagination(paginationRequestModel, stationEntityPage, resource);
        return resource;
    }

}
