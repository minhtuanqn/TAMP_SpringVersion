package com.tamp_backend.repository;

import com.tamp_backend.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SupplierRepository extends CrudRepository<SupplierEntity, UUID>, JpaSpecificationExecutor<SupplierEntity> {
}
