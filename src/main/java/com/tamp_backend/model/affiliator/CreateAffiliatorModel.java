package com.tamp_backend.model.affiliator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAffiliatorModel {
    private String username;

    private String email;

    private String password;

    private String fullName;

    private Date dob;

    private String phone;

    private int gender;

    private String address;

    private String code;

    private UUID partnerId;

    private UUID typeId;
}
