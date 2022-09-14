package com.tamp_backend.metamodel;

import com.tamp_backend.entity.PartnerTypeEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.util.UUID;

public class PartnerTypeEntity_ {
    public static volatile SingularAttribute<PartnerTypeEntity, UUID> id;
    public static volatile SingularAttribute<PartnerTypeEntity, String> typeName;
    public static volatile SingularAttribute<PartnerTypeEntity, String> description;
    public static volatile SingularAttribute<PartnerTypeEntity, Double> commissionRate;
    public static volatile SingularAttribute<PartnerTypeEntity, Integer> status;
    public static final String ID = "id";
    public static final String TYPE_NAME = "typeName";
    public static final String DESCRIPTION = "description";
    public static final String COMMISSION_RATE = "commissionRate";
    public static final String STATUS = "status";
}
