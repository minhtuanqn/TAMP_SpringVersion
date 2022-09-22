package com.tamp_backend.model.affiliator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.constant.EntityStatusEnum;
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
public class UpdateAffiliatorModel {
    private UUID id;

    private String email;

    private String fullName;

    private Date dob;

    private String phone;

    private int gender;

    private String address;

    private String code;

    private UUID typeId;

    @JsonProperty("affStatus")
    private EntityStatusEnum.AccountStatusEnum status;

}
