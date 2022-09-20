package com.tamp_backend.model.partnertype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerTypeFilterModel {

    private int statusType;

    private String typeName;
}
