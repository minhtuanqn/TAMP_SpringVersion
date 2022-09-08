package com.tamp_backend.repository;

import com.tamp_backend.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface CategoryRepository extends CrudRepository<CategoryEntity, UUID>, JpaSpecificationExecutor<CategoryEntity> {
    boolean existsCategoryEntityByName(String name);
    boolean existsCategoryEntityByNameAndIdNot(String name, UUID id);
}
