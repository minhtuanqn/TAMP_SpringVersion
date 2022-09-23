package com.tamp_backend.repository;

import com.tamp_backend.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CampaignRepository extends CrudRepository<CampaignEntity, UUID>, JpaSpecificationExecutor<CampaignEntity> {
    boolean existsByName(String name);
}
