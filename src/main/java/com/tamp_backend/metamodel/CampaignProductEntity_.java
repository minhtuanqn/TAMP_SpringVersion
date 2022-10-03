package com.tamp_backend.metamodel;

import com.tamp_backend.entity.CampaignCategoryEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.util.UUID;

public class CampaignProductEntity_ {
    public static volatile SingularAttribute<CampaignCategoryEntity, UUID> id;
    public static volatile SingularAttribute<CampaignCategoryEntity, UUID> productId;
    public static volatile SingularAttribute<CampaignCategoryEntity, UUID> campaignId;
    public static volatile SingularAttribute<CampaignCategoryEntity, Integer> status;

    public static final String ID = "id";
    public static final String PRODUCT_ID = "productEntity";
    public static final String CAMPAIGN_ID = "campaignEntity";
    public static final String STATUS = "status";
}
