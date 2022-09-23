package com.tamp_backend.constant;

public class StatusSearchEnum {
    /**
     * Status search enum of account entity
     */
    public enum AccountStatusSearchEnum {
        DISABLE, //0
        ACTIVE, //1
        ALL, //2
    }

    /**
     * Status search enum of category search status
     */
    public enum CategoryStatusSearchEnum {
        DISABLE, //0
        ACTIVE, //1
        ALL, //2
    }

    /**
     * Status search enum of partner type search status
     */
    public enum PartnerTypeStatusSearchEnum {
        DISABLE, //0
        ACTIVE, //1
        ALL, //2
    }

    /**
     * Status search enum of affiliator type search status
     */
    public enum AffiliatorTypeStatusSearchEnum {
        DISABLE, //0
        ACTIVE, //1
        ALL, //2
    }

    /**
     * Status search enum of product type search status
     */
    public enum ProductStatusSearchEnum
    {
        DELETED, // 0
        ACTIVE, //1
        PENDING, //2
        REJECTED, //3
        ALL, //4
    }

    /**
     * Status search enum of station search status
     */
    public enum StationStatusSearchEnum
    {
        DISABLE, //0
        ACTIVE, //1
        ALL, //2
    }
}
