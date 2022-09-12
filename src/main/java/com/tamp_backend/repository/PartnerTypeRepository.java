package com.tamp_backend.repository;

import com.tamp_backend.entity.PartnerTypeEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PartnerTypeRepository extends CrudRepository<PartnerTypeEntity, UUID>, JpaSpecificationExecutor<PartnerTypeEntity> {
    boolean existsPartnerTypeEntitiesByTypeName(String typeName);
    boolean existsPartnerTypeEntitiesByTypeNameAndIdNot(String typeName, UUID id);
}
