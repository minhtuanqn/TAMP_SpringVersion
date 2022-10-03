package com.tamp_backend.repository;

import com.tamp_backend.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.UUID;

public interface ProductRepository extends CrudRepository<ProductEntity, UUID>, JpaSpecificationExecutor<ProductEntity> {
    boolean existsByName(String name);
    @Query("select p.id from ProductEntity p where p.supplierEntity.id = ?1")
    Collection<UUID> findBySupplierId(UUID supplierId);
}
