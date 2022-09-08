package com.tamp_backend.entity;

import com.tamp_backend.model.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CategoryEntity {

    public CategoryEntity(CategoryModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.description = model.getDescription();
        this.commissionRate = model.getCommissionRate();
        this.status = model.getStatus();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "commission_rate")
    private Double commissionRate;

    @Column(name = "status")
    private Integer status;
}
