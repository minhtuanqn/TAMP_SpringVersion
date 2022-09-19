package com.tamp_backend.model.category;

import com.tamp_backend.entity.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {

    private UUID id;

    private String name;

    private String description;

    private Double commissionRate;

    private Integer status;
}
