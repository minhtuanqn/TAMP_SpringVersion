package com.tamp_backend.model.suppliercampaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCampaignFilterModel {
    private int statusType;

    private int supplierCampaignStatus;

    private String name;

    private boolean filterTime;

    private LocalDateTime minTime;

    private LocalDateTime maxTime;
}
