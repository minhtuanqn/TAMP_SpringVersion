package com.tamp_backend.repository;

import com.tamp_backend.entity.CampaignCategoryEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampaignCategoryRepository extends CrudRepository<CampaignCategoryEntity, UUID>, JpaSpecificationExecutor<CampaignCategoryEntity> {
    Optional<CampaignCategoryEntity> findCampaignCategoryEntitiesByCampaignEntityIdAndCategoryEntityId(UUID campaignId, UUID categoryId);
}
