package com.tamp_backend.metamodel;

import com.tamp_backend.entity.SupplierEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDateTime;
import java.util.UUID;

public class SupplierEntity_ {
    public static volatile SingularAttribute<SupplierEntity, UUID> id;
    public static volatile SingularAttribute<SupplierEntity, String> name;
    public static volatile SingularAttribute<SupplierEntity, String> phone;
    public static volatile SingularAttribute<SupplierEntity, String> logo;
    public static volatile SingularAttribute<SupplierEntity, String> description;
    public static volatile SingularAttribute<SupplierEntity, String> address;
    public static volatile SingularAttribute<SupplierEntity, LocalDateTime> createAt;
    public static volatile SingularAttribute<SupplierEntity, UUID> createBy;
    public static volatile SingularAttribute<SupplierEntity, LocalDateTime> updateAt;
    public static volatile SingularAttribute<SupplierEntity, UUID> accountId;
    public static volatile SingularAttribute<SupplierEntity, Integer> status;
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
}
