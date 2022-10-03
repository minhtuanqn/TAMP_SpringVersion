package com.tamp_backend.model.campaigncategory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.model.campaign.CampaignModel;
import com.tamp_backend.model.category.CategoryModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCategoryResponseModel {
    private UUID id;

    @JsonProperty("campaign")
    private CampaignModel campaignModel;

    @JsonProperty("category")
    private CategoryModel categoryModel;

    private double commissionRate;

    private int status;
}
