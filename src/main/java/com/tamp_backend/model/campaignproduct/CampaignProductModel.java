package com.tamp_backend.model.campaignproduct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignProductModel {

    private UUID id;

    private UUID campaignId;

    private UUID productId;

    private int status;
}
