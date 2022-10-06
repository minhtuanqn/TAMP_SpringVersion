package com.tamp_backend.model.campaignproduct;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.model.product.ProductModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignProductResponseModel {
    private UUID id;

    private UUID campaignId;
    @JsonProperty("product")
    private ProductModel productModel;

    private double netAffCommission;

    private double totalPartnerCommission;

    private double totalCommission;

    private int status;

}
