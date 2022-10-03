package com.tamp_backend.model.affiliatortype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AffiliatorTypeFilterModel {

    private int statusType;

    private String typeName;
}
