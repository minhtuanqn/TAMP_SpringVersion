package com.tamp_backend.repository;

import com.tamp_backend.entity.PartnerEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends CrudRepository<PartnerEntity, UUID>, JpaSpecificationExecutor<PartnerEntity> {
    Optional<PartnerEntity> findByAccountId(UUID accountId);
}
