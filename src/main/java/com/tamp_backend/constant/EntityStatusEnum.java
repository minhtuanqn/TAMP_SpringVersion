package com.tamp_backend.constant;

/**
 * Status enum of all entity in system
 */
public class EntityStatusEnum {

    /**
     * Status enum of category entity
     */
    public enum CategoryStatusEnum {
        DISABLE, //0
        ACTIVE, //1
    }

    /**
     * Status enum of partner type entity
     */
    public enum PartnerTypeStatusEnum {
        DISABLE, //0
        ACTIVE, //1
    }

    /**
     * Status enum of affiliator type entity
     */
    public enum AffiliatorTypeStatusEnum {
        DISABLE, //0
        ACTIVE, //1
    }

    /**
     * Status enum of account entity
     */
    public enum AccountStatusEnum {
        DISABLE, //0
        ACTIVE, //1
    }

    /**
     * Status enum of station entity
     */
    public enum StationStatusEnum {
        DISABLE, //0
        ACTIVE, //1
    }

    /**
     * Status of product entity
     */
    public enum ProductStatusEnum
    {
        DELETED, // 0
        ACTIVE, //1
        PENDING, //2
        REJECTED, //3
    }
    /**
     * Status of campaign entity
     */
    public enum CampaignStatusEnum
    {
        DELETED, // 0
        ACTIVE, //1
        PRODUCT_PREPARING, //2
        READY, //3
    }
    /**
     * Status of campaignCategory entity
     */
    public enum CampaignCategoryStatusEnum
    {
        DELETED, //0
        ACTIVE, //1
    }
}
