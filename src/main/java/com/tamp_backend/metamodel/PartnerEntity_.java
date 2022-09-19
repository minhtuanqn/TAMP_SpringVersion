package com.tamp_backend.metamodel;



import com.tamp_backend.entity.PartnerEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDateTime;
import java.util.UUID;

public class PartnerEntity_ {
    public static volatile SingularAttribute<PartnerEntity, UUID> id;
    public static volatile SingularAttribute<PartnerEntity, String> name;
    public static volatile SingularAttribute<PartnerEntity, String> phone;
    public static volatile SingularAttribute<PartnerEntity, String> logo;
    public static volatile SingularAttribute<PartnerEntity, String> description;
    public static volatile SingularAttribute<PartnerEntity, String> address;
    public static volatile SingularAttribute<PartnerEntity, LocalDateTime> createAt;
    public static volatile SingularAttribute<PartnerEntity, UUID> createBy;
    public static volatile SingularAttribute<PartnerEntity, LocalDateTime> updateAt;
    public static volatile SingularAttribute<PartnerEntity, UUID> accountId;
    public static volatile SingularAttribute<PartnerEntity, Integer> status;
    public static volatile SingularAttribute<PartnerEntity, UUID> typeId;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String LOGO = "logo";
    public static final String DESCRIPTION = "description";
    public static final String ADDRESS = "address";
    public static final String CREATE_AT = "createAt";
    public static final String CREATE_BY = "createBy";
    public static final String UPDATE_AT = "updateAt";
    public static final String ACCOUNT_ID = "accountId";
    public static final String STATUS = "status";
    public static final String TYPE_ID = "typeId";
}
