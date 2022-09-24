package com.tamp_backend.controller;


import com.tamp_backend.model.PaginationRequestModel;
import com.tamp_backend.model.ResourceModel;
import com.tamp_backend.model.station.StationFilterModel;
import com.tamp_backend.model.station.StationModel;
import com.tamp_backend.resolver.annotation.RequestPagingParam;
import com.tamp_backend.service.StationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping(path = "/stations")
public class StationController {
    private StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    /**
     * Create new station
     *
     * @param requestModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<StationModel> createStation(@Valid @RequestBody StationModel requestModel) {
        StationModel savedModel = stationService.createStation(requestModel);
        return new ResponseEntity<>(savedModel, HttpStatus.OK);
    }

    /**
     * delete station
     *
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<StationModel> deleteStation(@PathVariable UUID id) {
        StationModel deletedModel = stationService.deleteStation(id);
        return new ResponseEntity<>(deletedModel, HttpStatus.OK);
    }

    /**
     * Find station by id
     *
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<StationModel> findStationById(@PathVariable UUID id) {
        StationModel model = stationService.findStationById(id);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    /**
     * Update station
     *
     * @param id
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<StationModel> updateStation(@PathVariable UUID id,
                                                      @Valid @RequestBody StationModel requestModel) {
        StationModel updatedModel = stationService.updateStation(id, requestModel);
        return new ResponseEntity<>(updatedModel, HttpStatus.OK);
    }

    /**
     * Search stations by name
     *
     * @param searchText
     * @param paginationRequestModel
     * @return resource data of station
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasAuthority('SYSTEM_ADMIN')")
    public ResponseEntity<Object> searchStations(@RequestParam(value = "searchText", defaultValue = "") String searchText,
                                                 @RequestPagingParam PaginationRequestModel paginationRequestModel,
                                                 @ModelAttribute StationFilterModel stationFilterModel) {
        ResourceModel<StationModel> stationList = stationService.searchStations(searchText, paginationRequestModel, stationFilterModel);
        return new ResponseEntity<>(stationList, HttpStatus.OK);
    }
}
