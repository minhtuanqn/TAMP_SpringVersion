package com.tamp_backend.repository;

import com.tamp_backend.entity.CampaignProductEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface CampaignProductRepository extends CrudRepository<CampaignProductEntity, UUID>, JpaSpecificationExecutor<CampaignProductEntity> {
    Optional<CampaignProductEntity> findByCampaignEntityIdAndProductEntityId(UUID capaignId, UUID productId);
}
