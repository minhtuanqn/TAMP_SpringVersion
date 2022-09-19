package com.tamp_backend.model.partner;

import com.tamp_backend.constant.StatusSearchEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerFilterModel {
    private String name;

    private String phone;

    private String address;

    private StatusSearchEnum.AccountStatusSearchEnum statusType;
}
