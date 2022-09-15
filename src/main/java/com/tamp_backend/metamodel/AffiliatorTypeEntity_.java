package com.tamp_backend.metamodel;



import com.tamp_backend.entity.AffiliatorTypeEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.util.UUID;

public class AffiliatorTypeEntity_ {
    public static volatile SingularAttribute<AffiliatorTypeEntity, UUID> id;
    public static volatile SingularAttribute<AffiliatorTypeEntity, String> typeName;
    public static volatile SingularAttribute<AffiliatorTypeEntity, String> description;
    public static volatile SingularAttribute<AffiliatorTypeEntity, Double> commissionRate;
    public static volatile SingularAttribute<AffiliatorTypeEntity, Integer> status;

    public static final String ID = "id";
    public static final String TYPE_NAME = "typeName";
    public static final String DESCRIPTION = "description";
    public static final String COMMISSION_RATE = "commissionRate";
    public static final String STATUS = "status";
}
