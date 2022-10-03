package com.tamp_backend.model.campaigncategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignCategoryModel {
    private UUID categoryId;
    private double commissionRate;
    private int status;
}
