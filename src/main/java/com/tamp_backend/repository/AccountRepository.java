package com.tamp_backend.repository;

import com.tamp_backend.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {
    Optional<AccountEntity> findByUsername(String username);
}
