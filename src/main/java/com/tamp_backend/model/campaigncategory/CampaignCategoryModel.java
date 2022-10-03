package com.tamp_backend.model.campaigncategory;

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
public class CampaignCategoryModel {
    private UUID id;

    private UUID campaignId;

    private UUID categoryId;

    private double commissionRate;

    private int status;
}
