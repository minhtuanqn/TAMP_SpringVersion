package com.tamp_backend.repository;

import com.tamp_backend.entity.SystemAdminEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;
import java.util.UUID;

public interface SystemAdminRepository extends CrudRepository<SystemAdminEntity, UUID>,
        JpaSpecificationExecutor<SystemAdminEntity> {
    Optional<SystemAdminEntity> findByAccountId(UUID accountId);
}
