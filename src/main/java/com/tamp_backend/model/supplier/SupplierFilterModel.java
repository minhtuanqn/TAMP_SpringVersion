package com.tamp_backend.model.supplier;

import com.tamp_backend.constant.StatusSearchEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierFilterModel {

    private String name;

    private String phone;

    private String address;

    private int statusType;
}
