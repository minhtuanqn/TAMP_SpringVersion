package com.tamp_backend.repository;

import com.tamp_backend.entity.AffiliatorTypeEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
public interface AffiliatorTypeRepository extends CrudRepository<AffiliatorTypeEntity, UUID>, JpaSpecificationExecutor<AffiliatorTypeEntity> {
    boolean existsByTypeName(String typeName);

    boolean existsByTypeNameAndIdNot(String typeName, UUID id);
}
