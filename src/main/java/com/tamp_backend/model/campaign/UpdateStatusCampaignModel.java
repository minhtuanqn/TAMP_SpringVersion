package com.tamp_backend.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusCampaignModel {

    public UUID campaignId;

    public int campaignStatus;
}
