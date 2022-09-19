package com.tamp_backend.model.partner;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartnerModel {
    private String username;

    private String email;

    private String password;

    private String name;

    private String phone;

    private String description;

    private String address;

    private UUID typeId;
}
