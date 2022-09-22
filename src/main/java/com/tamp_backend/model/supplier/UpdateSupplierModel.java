package com.tamp_backend.model.supplier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.constant.EntityStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSupplierModel {

    private UUID id;

    private String email;

    private String name;

    private String phone;

    private String description;

    private String address;

    @JsonProperty("supplierStatus")
    private int status;
}
