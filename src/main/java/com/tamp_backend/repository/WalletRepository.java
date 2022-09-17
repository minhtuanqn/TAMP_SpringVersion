package com.tamp_backend.repository;

import com.tamp_backend.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface WalletRepository extends CrudRepository<WalletEntity, UUID>, JpaSpecificationExecutor<WalletEntity> {
}
