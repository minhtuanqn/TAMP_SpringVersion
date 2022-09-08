package com.tamp_backend.model;

import com.tamp_backend.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryModel {

    public CategoryModel(CategoryEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.commissionRate = entity.getCommissionRate();
        this.status = entity.getStatus();
    }

    private UUID id;

    private String name;

    private String description;

    private Double commissionRate;

    private Integer status;
}
