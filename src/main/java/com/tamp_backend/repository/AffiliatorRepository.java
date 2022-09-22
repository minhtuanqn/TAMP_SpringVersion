package com.tamp_backend.repository;

import com.tamp_backend.entity.AffiliatorEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AffiliatorRepository extends CrudRepository<AffiliatorEntity, UUID>, JpaSpecificationExecutor<AffiliatorEntity> {
    Optional<AffiliatorEntity> findByAccountId(UUID accountId);
}
