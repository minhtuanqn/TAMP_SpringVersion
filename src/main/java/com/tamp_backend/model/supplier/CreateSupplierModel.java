package com.tamp_backend.model.supplier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateSupplierModel {

    private String username;

    private String email;

    private String password;

    private String name;

    private String phone;

    private String description;

    private String address;

}
