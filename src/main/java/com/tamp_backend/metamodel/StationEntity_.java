package com.tamp_backend.metamodel;



import com.tamp_backend.entity.StationEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Date;
import java.util.UUID;

public class StationEntity_ {
    public static volatile SingularAttribute<StationEntity, UUID> id;
    public static volatile SingularAttribute<StationEntity, String> name;
    public static volatile SingularAttribute<StationEntity, Double> longitude;
    public static volatile SingularAttribute<StationEntity, Double> latitude;
    public static volatile SingularAttribute<StationEntity, String> address;
    public static volatile SingularAttribute<StationEntity, String> detailAddress;
    public static volatile SingularAttribute<StationEntity, Date> createAt;
    public static volatile SingularAttribute<StationEntity, Integer> status;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String ADDRESS = "address";
    public static final String DETAIL_ADDRESS = "detailAddress";
    public static final String CREATE_AT = "createAt";
    public static final String STATUS = "status";
}
