package com.tamp_backend.model.campaigncategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCategoryFilterModel {
    private int campaignCategoryStatus;
    private int categoryStatus;
    private String categoryName;
}
