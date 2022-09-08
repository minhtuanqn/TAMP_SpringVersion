package com.tamp_backend.metamodel;

import com.tamp_backend.entity.CategoryEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.util.UUID;

public class CategoryEntity_ {
    public static volatile SingularAttribute<CategoryEntity, UUID> id;
    public static volatile SingularAttribute<CategoryEntity, String> name;
    public static volatile SingularAttribute<CategoryEntity, String> description;
    public static volatile SingularAttribute<CategoryEntity, Double> commissionRate;
    public static volatile SingularAttribute<CategoryEntity, Integer> status;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String COMMISSION_RATE = "commissionRate";
    public static final String STATUS = "status";
}
