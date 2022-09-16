package com.tamp_backend.repository;

import com.tamp_backend.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StationRepository extends CrudRepository<StationEntity, UUID> , JpaSpecificationExecutor<StationEntity> {

}
