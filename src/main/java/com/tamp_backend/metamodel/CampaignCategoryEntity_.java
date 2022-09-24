package com.tamp_backend.metamodel;



import com.tamp_backend.entity.CampaignCategoryEntity;

import javax.persistence.metamodel.SingularAttribute;
import java.util.UUID;

public class CampaignCategoryEntity_ {
    public static volatile SingularAttribute<CampaignCategoryEntity, UUID> id;
    public static volatile SingularAttribute<CampaignCategoryEntity, UUID> campaignId;
    public static volatile SingularAttribute<CampaignCategoryEntity, UUID> categoryId;
    public static volatile SingularAttribute<CampaignCategoryEntity, Double> commissionRate;
    public static volatile SingularAttribute<CampaignCategoryEntity, Integer> status;

    public static final String ID = "id";
    public static final String CAMPAIGN_ID = "campaignId";
    public static final String CATEGORY_ID = "categoryId";
    public static final String COMMISSION_RATE = "commissionRate";
    public static final String STATUS = "status";


}
