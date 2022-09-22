package com.tamp_backend.model.affiliator;

import com.tamp_backend.constant.StatusSearchEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AffiliatorFilterModel {
    private String fullName;

    private String phone;

    private String address;

    private StatusSearchEnum.AccountStatusSearchEnum statusType;
}
