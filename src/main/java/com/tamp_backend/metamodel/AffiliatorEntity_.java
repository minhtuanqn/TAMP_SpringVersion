package com.tamp_backend.metamodel;


import com.tamp_backend.entity.AffiliatorEntity;
import com.tamp_backend.entity.PartnerEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class AffiliatorEntity_ {
    public static volatile SingularAttribute<AffiliatorEntity, UUID> id;
    public static volatile SingularAttribute<AffiliatorEntity, Date> dob;
    public static volatile SingularAttribute<AffiliatorEntity, String> phone;
    public static volatile SingularAttribute<AffiliatorEntity, Integer> gender;
    public static volatile SingularAttribute<AffiliatorEntity, String> fullName;
    public static volatile SingularAttribute<AffiliatorEntity, String> address;
    public static volatile SingularAttribute<AffiliatorEntity, String> avatar;
    public static volatile SingularAttribute<AffiliatorEntity, UUID> partnerId;
    public static volatile SingularAttribute<AffiliatorEntity, String> code;
    public static volatile SingularAttribute<PartnerEntity, LocalDateTime> createAt;
    public static volatile SingularAttribute<PartnerEntity, LocalDateTime> updateAt;
    public static volatile SingularAttribute<PartnerEntity, UUID> accountId;
    public static volatile SingularAttribute<PartnerEntity, Integer> status;
    public static volatile SingularAttribute<PartnerEntity, UUID> typeId;

    public static final String ID = "id";
    public static final String DOB = "dob";
    public static final String PHONE = "phone";
    public static final String GENDER = "gender";
    public static final String FULL_NAME = "fullName";
    public static final String ADDRESS = "address";
    public static final String AVATAR = "avatar";
    public static final String PARTNER_ID = "partnerId";
    public static final String CODE = "code";

    public static final String CREATE_AT = "createAt";
    public static final String UPDATE_AT = "updateAt";
    public static final String ACCOUNT_ID = "accountId";
    public static final String STATUS = "status";
    public static final String TYPE_ID = "typeId";
}
